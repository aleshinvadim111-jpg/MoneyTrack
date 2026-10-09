package com.example.moneytrack.data

import com.example.moneytrack.model.Category
import com.example.moneytrack.model.DemoData
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.SubPeriod
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed interface AuthResult {
    data object Success : AuthResult
    data class Error(val message: String) : AuthResult
}

/**
 * Единая точка доступа к данным. Экраны не трогают базу напрямую:
 * они получают StateFlow со списками и вызывают методы репозитория.
 * Запросы Room выполняются в фоне (suspend-функции DAO).
 */
class MoneyRepository(db: AppDatabase, private val session: SessionManager) {
    private val users = db.userDao()
    private val expenseDao = db.expenseDao()
    private val subscriptionDao = db.subscriptionDao()

    private val _notificationsEnabled = MutableStateFlow(session.notificationsEnabled)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    // ---------- авторизация ----------

    /** При запуске: если уже входили — восстанавливаем пользователя */
    suspend fun restoreSession(): Boolean {
        val id = session.userId ?: return false
        val found = users.findById(id)
        if (found == null) {
            session.clear()
            return false
        }
        _user.value = found.toModel()
        reload()
        return true
    }

    suspend fun register(name: String, email: String, password: String): AuthResult {
        val normalized = email.trim().lowercase()
        if (users.findByEmail(normalized) != null) {
            return AuthResult.Error("Этот email уже зарегистрирован")
        }
        val salt = PasswordHasher.newSalt()
        val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(password, salt) }
        val id = users.insert(UserEntity(name = name.trim(), email = normalized, passwordHash = hash, salt = salt))
        session.userId = id
        _user.value = users.findById(id)?.toModel()
        reload()
        return AuthResult.Success
    }

    suspend fun login(email: String, password: String): AuthResult {
        val found = users.findByEmail(email.trim().lowercase())
        val ok = found != null && withContext(Dispatchers.Default) {
            PasswordHasher.verify(password, found.salt, found.passwordHash)
        }
        if (found == null || !ok) return AuthResult.Error("Неверный email или пароль")
        session.userId = found.id
        _user.value = found.toModel()
        reload()
        return AuthResult.Success
    }

    fun logout() {
        session.clear()
        _user.value = null
        _expenses.value = emptyList()
        _subscriptions.value = emptyList()
    }

    suspend fun setLimit(limit: Long) {
        val u = _user.value ?: return
        users.updateLimit(u.id, limit)
        _user.value = u.copy(monthlyLimit = limit)
    }

    // ---------- траты ----------

    suspend fun addExpense(category: Category, amount: Long, dateMillis: Long, note: String) {
        val u = _user.value ?: return
        expenseDao.insert(Expense(0, category, note, amount, dateMillis).toEntity(u.id))
        reload()
    }

    /** Трата из уже загруженного списка (для формы редактирования) */
    fun findExpense(id: Long): Expense? = _expenses.value.firstOrNull { it.id == id }

    suspend fun updateExpense(expense: Expense) {
        val u = _user.value ?: return
        expenseDao.update(expense.toEntity(u.id))
        reload()
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.id)
        reload()
    }

    /** «Отменить» в снекбаре: возвращаем удалённую трату с тем же id */
    suspend fun restoreExpense(expense: Expense) {
        val u = _user.value ?: return
        expenseDao.insert(expense.toEntity(u.id))
        reload()
    }

    // ---------- подписки ----------

    suspend fun addSubscription(subscription: Subscription) {
        val u = _user.value ?: return
        subscriptionDao.insert(subscription.copy(id = 0).toEntity(u.id))
        reload()
    }

    fun findSubscription(id: Long): Subscription? = _subscriptions.value.firstOrNull { it.id == id }

    suspend fun updateSubscription(subscription: Subscription) {
        val u = _user.value ?: return
        subscriptionDao.update(subscription.toEntity(u.id))
        reload()
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.delete(subscription.id)
        reload()
    }

    // ---------- уведомления ----------

    fun setNotificationsEnabled(enabled: Boolean) {
        session.notificationsEnabled = enabled
        _notificationsEnabled.value = enabled
    }

    /**
     * Подписки вошедшего пользователя прямо из базы.
     * Нужно фоновой задаче: она может запуститься, когда приложение закрыто и данные не загружены.
     */
    suspend fun subscriptionsForReminders(): List<Subscription> {
        val id = session.userId ?: return emptyList()
        return subscriptionDao.forUser(id).map { it.toModel() }
    }

    /** Сколько потрачено в текущем месяце */
    fun spentThisMonth(): Long = _expenses.value.filter { Dates.isThisMonth(it.dateMillis) }.sumOf { it.amount }

    // ---------- демо-данные для защиты ----------

    /** Заполняет пустой аккаунт примерами — удобно показать приложение «в деле» */
    suspend fun fillDemoData() {
        val u = _user.value ?: return
        if (expenseDao.count(u.id) == 0) {
            DemoData.expenses().forEach { expenseDao.insert(it.copy(id = 0).toEntity(u.id)) }
        }
        if (subscriptionDao.count(u.id) == 0) {
            DemoData.subscriptions().forEach { subscriptionDao.insert(it.copy(id = 0).toEntity(u.id)) }
        }
        reload()
    }

    // ---------- внутреннее ----------

    /** Перечитываем данные текущего пользователя; прошедшие списания подписок сдвигаем на следующий период */
    private suspend fun reload() {
        val u = _user.value
        if (u == null) {
            _expenses.value = emptyList()
            _subscriptions.value = emptyList()
            return
        }
        val today = Dates.startOfDay(System.currentTimeMillis())
        subscriptionDao.forUser(u.id).forEach { s ->
            if (s.nextCharge < today) {
                var next = s.nextCharge
                while (next < today) next = Dates.addPeriod(next, yearly = s.period == SubPeriod.YEAR.name)
                subscriptionDao.updateNextCharge(s.id, next)
            }
        }
        _expenses.value = expenseDao.forUser(u.id).map { it.toModel() }
        _subscriptions.value = subscriptionDao.forUser(u.id).map { it.toModel() }
    }
}
