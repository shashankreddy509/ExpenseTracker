package com.shashank.expense.tracker.screens.homescreen

import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.models.CategoryModel
import com.shashank.expense.tracker.models.ExpenseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import com.shashank.expense.tracker.utils.DateTimeUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class SpendingPoint(
    val value: Float,
    val label: String,
    val timestamp: LocalDateTime
)

class HomeViewModel : KoinComponent {
    private val databaseHelper: DatabaseHelper by inject()
    private val scope = CoroutineScope(Dispatchers.Main)

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

    private val _selectedTimeFrame = MutableStateFlow("Month")
    val selectedTimeFrame: StateFlow<String> = _selectedTimeFrame.asStateFlow()

    private val _spendingData = MutableStateFlow<List<SpendingPoint>>(emptyList())
    val spendingData: StateFlow<List<SpendingPoint>> = _spendingData.asStateFlow()

    private val _selectedMonth = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month)
    val selectedMonth: StateFlow<Month> = _selectedMonth.asStateFlow()

    private val _filteredExpenses = MutableStateFlow<List<ExpenseModel>>(emptyList())
    val filteredExpenses: StateFlow<List<ExpenseModel>> = _filteredExpenses.asStateFlow()

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType.asStateFlow()

    private val _dateRange = MutableStateFlow<Pair<Long, Long>?>(null)
    val dateRange: StateFlow<Pair<Long, Long>?> = _dateRange.asStateFlow()

    init {
        loadData()
        scope.launch {
            // Watch for search and filter changes
            combine(
                searchQuery,
                selectedCategory,
                selectedType,
                dateRange,
                expenses
            ) { query, category, type, range, allExpenses ->
                when {
                    !query.isBlank() -> databaseHelper.searchExpenses(query)
                    category != null -> databaseHelper.filterExpensesByCategory(category)
                    type != null -> databaseHelper.filterExpensesByType(type)
                    range != null -> databaseHelper.filterExpensesByDateRange(range.first, range.second)
                    else -> flow { emit(allExpenses) }
                }
            }.collect { filteredFlow ->
                filteredFlow.collect { filtered ->
                    _filteredExpenses.value = filtered
                    updateBalances(filtered)
                    updateSpendingData()
                }
            }
        }

        scope.launch {
            // Watch for month changes to update filtered expenses
            combine(expenses, selectedMonth) { expensesList, month ->
                val (startTime, endTime) = DateTimeUtils.getMonthStartEnd(month)
                expensesList.filter {
                    try {
                        val timestamp = it.date
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
                _expenses.value = dbExpenses
                updateBalances()
            }

            // Collect categories
            databaseHelper.getAllCategories().collect { dbCategories ->
                _categories.value = dbCategories
            }

            // Collect favorite categories
            databaseHelper.getFavoriteCategories().collect { dbCategories ->
                _favoriteCategories.value = dbCategories
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

    fun addExpense(expense: ExpenseModel) {
        scope.launch {
            databaseHelper.insertExpense(
                title = expense.title,
                amount = expense.amount,
                category = expense.category,
                type = expense.type,
                tax = expense.tax,
                date = expense.date
            )
        }
    }

    fun updateExpense(expense: ExpenseModel) {
        scope.launch {
            // TODO: Implement update expense
        }
    }

    fun deleteExpense(id: Long) {
        scope.launch {
            // TODO: Implement delete expense
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
                    val expenseDate = Instant.fromEpochMilliseconds(it.date)
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
                    val expenseDate = Instant.fromEpochMilliseconds(it.date)
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
                    val expenseDate = Instant.fromEpochMilliseconds(it.date)
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
                    val expenseDate = Instant.fromEpochMilliseconds(it.date)
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

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSelectedType(type: String?) {
        _selectedType.value = type
    }

    fun setDateRange(startDate: Long, endDate: Long) {
        _dateRange.value = Pair(startDate, endDate)
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedType.value = null
        _dateRange.value = null
    }
}