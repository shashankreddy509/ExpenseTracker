package viewmodels

import com.shashank.expense.tracker.db.Category as DBCategory
import com.shashank.expense.tracker.db.Expense as DBExpense
import data.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import screens.CategoryModel
import screens.ExpenseModel
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.hours
import utils.DateTimeUtils

data class SpendingPoint(
    val value: Float,
    val label: String,
    val timestamp: LocalDateTime
)

class HomeViewModel(
    private val databaseHelper: DatabaseHelper,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _expenses = MutableStateFlow<List<ExpenseModel>>(emptyList())
    val expenses: StateFlow<List<ExpenseModel>> = _expenses.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    private val _favoriteCategories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val favoriteCategories: StateFlow<List<CategoryModel>> = _favoriteCategories.asStateFlow()

    private val _totalBalance = MutableStateFlow(0.0)
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()

    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()

    private val _selectedTimeFrame = MutableStateFlow("Week")
    val selectedTimeFrame: StateFlow<String> = _selectedTimeFrame

    private val _spendingData = MutableStateFlow<List<SpendingPoint>>(emptyList())
    val spendingData: StateFlow<List<SpendingPoint>> = _spendingData

    private val _selectedMonth = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month)
    val selectedMonth: StateFlow<Month> = _selectedMonth

    private val _filteredExpenses = MutableStateFlow<List<ExpenseModel>>(emptyList())
    val filteredExpenses: StateFlow<List<ExpenseModel>> = _filteredExpenses

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError

    init {
        loadData()
        scope.launch {
            // Watch for month changes to update filtered expenses
            combine(expenses, selectedMonth) { expensesList, month ->
                val (startTime, endTime) = DateTimeUtils.getMonthStartEnd(month)
                expensesList.filter {
                    try {
                        val timestamp = it.date.toLong()
                        timestamp in startTime..endTime
                    } catch (e: Exception) {
                        false
                    }
                }
            }.collect { filtered ->
                _filteredExpenses.value = filtered
                updateBalances(filtered)
                updateSpendingData()
            }
        }
    }

    private fun loadData() {
        scope.launch {
            // Collect expenses
            databaseHelper.getAllExpenses().collect { dbExpenses ->
                _expenses.value = dbExpenses.map { expense ->
                    ExpenseModel(
                        id = expense.id,
                        title = expense.title,
                        amount = expense.amount,
                        category = expense.category,
                        isIncome = expense.type == "income",
                        date = expense.date
                    )
                }
                updateBalances()
            }

            // Collect categories
            databaseHelper.getAllCategories().collect { dbCategories ->
                _categories.value = dbCategories.map { category ->
                    CategoryModel(
                        id = category.id,
                        title = category.name,
                        isFavorite = category.isFavorite
                    )
                }
            }

            // Collect favorite categories
            databaseHelper.getFavoriteCategories().collect { dbCategories ->
                _favoriteCategories.value = dbCategories.map { category ->
                    CategoryModel(
                        id = category.id,
                        title = category.name,
                        isFavorite = category.isFavorite
                    )
                }
            }
        }
    }

    private fun updateBalances(expenses: List<ExpenseModel> = _filteredExpenses.value) {
        _totalIncome.value = expenses.filter { it.isIncome }.sumOf { it.amount }
        _totalExpenses.value = expenses.filter { !it.isIncome }.sumOf { it.amount }
        _totalBalance.value = _totalIncome.value - _totalExpenses.value
    }

    fun toggleCategoryFavorite(category: CategoryModel) {
        scope.launch {
            databaseHelper.updateCategoryFavorite(category.id, !category.isFavorite)
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        timestamp: Long = DateTimeUtils.getCurrentDateTime()
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    ) {
        if (!validateExpense(title, amount, category, timestamp)) {
            return
        }

        scope.launch {
            databaseHelper.insertExpense(
                title = title,
                amount = amount,
                category = category,
                type = if (isIncome) "income" else "expense",
                tax = 0.0,
                date = timestamp
            )
            _dateError.value = null
        }
    }

    private fun validateExpense(
        title: String,
        amount: Double,
        category: String,
        timestamp: Long
    ): Boolean {
        if (title.isBlank()) {
            return false
        }
        if (amount <= 0) {
            return false
        }
        if (category.isBlank()) {
            return false
        }
        if (!DateTimeUtils.isValidDate(timestamp)) {
            _dateError.value = "Invalid date or date is in future"
            return false
        }
        return true
    }

    suspend fun loadCategories() {
        databaseHelper.getAllCategories().collect { dbCategories ->
            _categories.value = dbCategories.map { category ->
                CategoryModel(
                    id = category.id,
                    title = category.name,
                    isFavorite = category.isFavorite
                )
            }
        }
    }

    fun updateTimeFrame(timeFrame: String) {
        _selectedTimeFrame.value = timeFrame
    }

    private suspend fun updateSpendingData() {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val data = when (_selectedTimeFrame.value) {
            "Today" -> getHourlySpending(currentTime)
            "Week" -> getDailySpending(currentTime)
            "Month" -> getWeeklySpending(currentTime)
            "Year" -> getMonthlySpending(currentTime)
            else -> getDailySpending(currentTime)
        }
        _spendingData.value = data
    }

    private suspend fun getHourlySpending(currentTime: LocalDateTime): List<SpendingPoint> {
        val expenses = _filteredExpenses.value
        return (0..23).map { hour ->
            val hourExpenses = expenses.filter {
                try {
                    val expenseDate = Instant.fromEpochMilliseconds(it.date.toLong())
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    expenseDate.hour == hour && expenseDate.date == currentTime.date
                } catch (e: Exception) {
                    false
                }
            }
            SpendingPoint(
                value = hourExpenses.sumOf { it.amount }.toFloat(),
                label = "${hour}:00",
                timestamp = currentTime.date.atTime(hour, 0)
            )
        }
    }

    private suspend fun getDailySpending(currentTime: LocalDateTime): List<SpendingPoint> {
        val expenses = _filteredExpenses.value
        return (6 downTo 0).map { daysAgo ->
            val date = currentTime.date.minus(DatePeriod(days = daysAgo))
            val dayExpenses = expenses.filter {
                try {
                    val expenseDate = Instant.fromEpochMilliseconds(it.date.toLong())
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    expenseDate.date == date
                } catch (e: Exception) {
                    false
                }
            }
            SpendingPoint(
                value = dayExpenses.sumOf { it.amount }.toFloat(),
                label = when (daysAgo) {
                    0 -> "Today"
                    1 -> "Yesterday"
                    else -> date.dayOfWeek.name.take(3)
                },
                timestamp = LocalDateTime(date, LocalTime(0, 0))
            )
        }
    }

    private suspend fun getWeeklySpending(currentTime: LocalDateTime): List<SpendingPoint> {
        val expenses = _filteredExpenses.value
        return (3 downTo 0).map { weekAgo ->
            val startDate = currentTime.date.minus(DatePeriod(days = weekAgo * 7))
            val endDate = startDate.plus(DatePeriod(days = 6))
            val weekExpenses = expenses.filter {
                try {
                    val expenseDate = Instant.fromEpochMilliseconds(it.date.toLong())
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    expenseDate.date in startDate..endDate
                } catch (e: Exception) {
                    false
                }
            }
            SpendingPoint(
                value = weekExpenses.sumOf { it.amount }.toFloat(),
                label = "Week ${4 - weekAgo}",
                timestamp = LocalDateTime(startDate, LocalTime(0, 0))
            )
        }
    }

    private suspend fun getMonthlySpending(currentTime: LocalDateTime): List<SpendingPoint> {
        val expenses = _filteredExpenses.value
        return (11 downTo 0).map { monthAgo ->
            val date = currentTime.date.minus(DatePeriod(months = monthAgo))
            val monthExpenses = expenses.filter {
                try {
                    val expenseDate = Instant.fromEpochMilliseconds(it.date.toLong())
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    expenseDate.date.month == date.month
                } catch (e: Exception) {
                    false
                }
            }
            SpendingPoint(
                value = monthExpenses.sumOf { it.amount }.toFloat(),
                label = date.month.name.take(3),
                timestamp = LocalDateTime(date, LocalTime(0, 0))
            )
        }
    }

    fun selectMonth(month: Month) {
        _selectedMonth.value = month
    }
} 