package com.example.moneytrack.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneytrack.AppContainer
import com.example.moneytrack.ui.components.FloatingNavBar
import com.example.moneytrack.ui.components.MainTab
import com.example.moneytrack.ui.components.rememberNotificationPermissionRequest
import com.example.moneytrack.ui.screens.ExpenseFormScreen
import com.example.moneytrack.ui.screens.HistoryScreen
import com.example.moneytrack.ui.screens.HomeScreen
import com.example.moneytrack.ui.screens.LoginScreen
import com.example.moneytrack.ui.screens.ProfileScreen
import com.example.moneytrack.ui.screens.RegisterScreen
import com.example.moneytrack.ui.screens.StatsScreen
import com.example.moneytrack.ui.screens.SubscriptionFormScreen
import com.example.moneytrack.ui.screens.SubscriptionsScreen
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.viewmodel.AppViewModelFactory
import com.example.moneytrack.ui.viewmodel.AuthViewModel
import com.example.moneytrack.ui.viewmodel.ExpenseFormViewModel
import com.example.moneytrack.ui.viewmodel.HistoryViewModel
import com.example.moneytrack.ui.viewmodel.HomeViewModel
import com.example.moneytrack.ui.viewmodel.ProfileViewModel
import com.example.moneytrack.ui.viewmodel.SessionViewModel
import com.example.moneytrack.ui.viewmodel.StatsViewModel
import com.example.moneytrack.ui.viewmodel.SubscriptionFormViewModel
import com.example.moneytrack.ui.viewmodel.SubscriptionsViewModel

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HISTORY = "history"

    // Формы открываются в двух режимах: без id — создание, с id — редактирование
    const val EXPENSE_FORM = "expense_form?id={id}"
    const val SUBSCRIPTION_FORM = "subscription_form?id={id}"

    fun expenseForm(id: Long? = null) = if (id == null) "expense_form" else "expense_form?id=$id"
    fun subscriptionForm(id: Long? = null) = if (id == null) "subscription_form" else "subscription_form?id=$id"

    /** Необязательный аргумент id: -1 значит «новая запись» */
    val idArgument = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
}

/**
 * Корень приложения. Схема MVVM:
 * экран (View) получает состояние из своей ViewModel и передаёт ей действия пользователя,
 * ViewModel обращается к репозиториям: MoneyRepository (Room + SharedPreferences)
 * и CurrencyRepository (Retrofit → API ЦБ, кэш в Room).
 */
@Composable
fun MoneyTrackApp(container: AppContainer) {
    val factory = remember(container) { AppViewModelFactory(container) }
    val session: SessionViewModel = viewModel(factory = factory)
    val startLoggedIn by session.startLoggedIn.collectAsState()

    // Пока проверяем сохранённую сессию — пустой «бумажный» экран (доли секунды)
    val loggedIn = startLoggedIn
    if (loggedIn == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Paper)
        )
        return
    }
    val start = if (loggedIn) MainTab.Home.route else Routes.LOGIN

    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentTab = MainTab.entries.firstOrNull { it.route == backStack?.destination?.route }

    Box(
        Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        NavHost(navController = nav, startDestination = start) {
            composable(Routes.LOGIN) {
                val vm: AuthViewModel = viewModel(factory = factory)
                val error by vm.error.collectAsState()
                LoginScreen(
                    onLogin = { email, password -> vm.login(email, password) { nav.goHome() } },
                    onRegister = {
                        vm.clearError()
                        nav.navigate(Routes.REGISTER)
                    },
                    error = error,
                )
            }
            composable(Routes.REGISTER) {
                val vm: AuthViewModel = viewModel(factory = factory)
                val error by vm.error.collectAsState()
                RegisterScreen(
                    onBack = { nav.popBackStack() },
                    onRegister = { name, email, password -> vm.register(name, email, password) { nav.goHome() } },
                    error = error,
                )
            }
            composable(MainTab.Home.route) {
                val vm: HomeViewModel = viewModel(factory = factory)
                val state by vm.uiState.collectAsState()
                // Android 13+: один раз спрашиваем разрешение на уведомления, когда пользователь попал на Главную
                val requestNotifications = rememberNotificationPermissionRequest()
                LaunchedEffect(Unit) {
                    if (container.moneyRepository.notificationsEnabled.value) requestNotifications()
                }
                HomeScreen(
                    userName = state.userName,
                    initials = state.initials,
                    spent = state.spent,
                    limit = state.limit,
                    todayExpenses = state.todayExpenses,
                    nearestSubscription = state.nearestSubscription,
                    usdRate = state.usdRate,
                    onOpenHistory = { nav.navigate(Routes.HISTORY) },
                    onOpenProfile = { nav.navigate(MainTab.Profile.route) { launchSingleTop = true } },
                    onOpenExpense = { nav.navigate(Routes.expenseForm(it.id)) },
                )
            }
            composable(MainTab.Stats.route) {
                val vm: StatsViewModel = viewModel(factory = factory)
                val expenses by vm.expenses.collectAsState()
                StatsScreen(expenses = expenses)
            }
            composable(MainTab.Subscriptions.route) {
                val vm: SubscriptionsViewModel = viewModel(factory = factory)
                val subscriptions by vm.subscriptions.collectAsState()
                SubscriptionsScreen(
                    subscriptions = subscriptions,
                    onOpen = { nav.navigate(Routes.subscriptionForm(it.id)) },
                )
            }
            composable(MainTab.Profile.route) {
                val vm: ProfileViewModel = viewModel(factory = factory)
                val user by vm.user.collectAsState()
                val rates by vm.rates.collectAsState()
                val notifications by vm.notificationsEnabled.collectAsState()
                val requestNotifications = rememberNotificationPermissionRequest()
                val u = user
                ProfileScreen(
                    name = u?.name ?: "",
                    email = u?.email ?: "",
                    initials = u?.initials ?: "",
                    limit = u?.monthlyLimit ?: 0,
                    onLimitChange = vm::setLimit,
                    onFillDemo = vm::fillDemoData,
                    rates = rates,
                    onRefreshRates = vm::refreshRates,
                    notifications = notifications,
                    onNotificationsChange = { enabled ->
                        vm.setNotificationsEnabled(enabled)
                        if (enabled) requestNotifications()
                    },
                    onCheckReminders = vm::checkRemindersNow,
                    onLogout = {
                        vm.logout()
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.HISTORY) {
                val vm: HistoryViewModel = viewModel(factory = factory)
                val expenses by vm.expenses.collectAsState()
                HistoryScreen(
                    expenses = expenses,
                    onBack = { nav.popBackStack() },
                    onDelete = vm::delete,
                    onRestore = vm::restore,
                    onOpen = { nav.navigate(Routes.expenseForm(it.id)) },
                )
            }
            composable(Routes.EXPENSE_FORM, arguments = Routes.idArgument) { entry ->
                val vm: ExpenseFormViewModel = viewModel(factory = factory)
                val id = entry.arguments?.getLong("id")?.takeIf { it > 0 }
                val editing = remember(id) { vm.find(id) }
                ExpenseFormScreen(
                    initial = editing,
                    onClose = { nav.popBackStack() },
                    onSave = { category, amount, date, note ->
                        vm.save(editing, category, amount, date, note) { nav.popBackStack() }
                    },
                    onDelete = { editing?.let { vm.delete(it) { nav.popBackStack() } } },
                )
            }
            composable(Routes.SUBSCRIPTION_FORM, arguments = Routes.idArgument) { entry ->
                val vm: SubscriptionFormViewModel = viewModel(factory = factory)
                val id = entry.arguments?.getLong("id")?.takeIf { it > 0 }
                val editing = remember(id) { vm.find(id) }
                SubscriptionFormScreen(
                    initial = editing,
                    onClose = { nav.popBackStack() },
                    onSave = { subscription -> vm.save(subscription) { nav.popBackStack() } },
                    onDelete = { editing?.let { vm.delete(it) { nav.popBackStack() } } },
                )
            }
        }

        // Плавающая панель — только на четырёх главных вкладках
        if (currentTab != null) {
            FloatingNavBar(
                current = currentTab,
                onSelect = { tab ->
                    nav.navigate(tab.route) {
                        popUpTo(MainTab.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                // Контекстная кнопка «+»: на вкладке Подписки — новая подписка, иначе — новый расход
                onAdd = {
                    nav.navigate(
                        if (currentTab == MainTab.Subscriptions) Routes.subscriptionForm() else Routes.expenseForm()
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
            )
        }
    }
}

/** После входа или регистрации — на Главную, экраны входа убираем из истории */
private fun NavHostController.goHome() {
    navigate(MainTab.Home.route) {
        popUpTo(graph.id) { inclusive = true }
    }
}
