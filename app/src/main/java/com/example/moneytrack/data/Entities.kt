package com.example.moneytrack.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moneytrack.model.Category
import com.example.moneytrack.model.CurrencyRate
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.SubPeriod
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.model.User

/** Таблица users. Пароль хранится только как хэш + соль. */
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    val salt: String,
    @ColumnInfo(name = "monthly_limit") val monthlyLimit: Long = 30_000,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
)

/** Таблица expenses. Удаление пользователя удаляет и его траты (CASCADE). */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["user_id", "date"])],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    val category: String,
    val amount: Long,
    val date: Long,
    val comment: String,
)

/** Таблица subscriptions */
@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["user_id"])],
)
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    val title: String,
    val amount: Long,
    @ColumnInfo(name = "next_charge") val nextCharge: Long,
    val period: String,
    val color: Int,
    val remind: Boolean,
)

/** Таблица currency_rates — кэш курсов ЦБ, чтобы приложение работало без интернета */
@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val code: String,
    val name: String,
    val nominal: Int,
    val value: Double,
    val previous: Double,
    @ColumnInfo(name = "cbr_date") val cbrDate: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)

// ---------- преобразование «таблица ↔ модель для экранов» ----------

fun CurrencyRateEntity.toModel() = CurrencyRate(
    code = code,
    name = name,
    nominal = nominal,
    value = value,
    previous = previous,
    cbrDate = cbrDate,
    updatedAt = updatedAt,
)

fun UserEntity.toModel() = User(id = id, name = name, email = email, monthlyLimit = monthlyLimit)

fun ExpenseEntity.toModel() = Expense(
    id = id,
    category = runCatching { Category.valueOf(category) }.getOrDefault(Category.OTHER),
    note = comment,
    amount = amount,
    dateMillis = date,
)

fun Expense.toEntity(userId: Long) = ExpenseEntity(
    id = id,
    userId = userId,
    category = category.name,
    amount = amount,
    date = dateMillis,
    comment = note,
)

fun SubscriptionEntity.toModel() = Subscription(
    id = id,
    title = title,
    amount = amount,
    nextChargeMillis = nextCharge,
    period = runCatching { SubPeriod.valueOf(period) }.getOrDefault(SubPeriod.MONTH),
    color = Color(color),
    remind = remind,
)

fun Subscription.toEntity(userId: Long) = SubscriptionEntity(
    id = id,
    userId = userId,
    title = title,
    amount = amount,
    nextCharge = nextChargeMillis,
    period = period.name,
    color = color.toArgb(),
    remind = remind,
)
