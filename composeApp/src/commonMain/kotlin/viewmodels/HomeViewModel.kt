package viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.DatabaseHelper
import data.DatabaseHelper.CategoryModel
import data.DatabaseHelper.ExpenseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val databaseHelper: DatabaseHelper) {
    private val _expenses = MutableStateFlow<List<ExpenseModel>>(emptyList())
    val expenses: StateFlow<List<ExpenseModel>> = _expenses.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    private val _favoriteCategories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val favoriteCategories: StateFlow<List<CategoryModel>> = _favoriteCategories.asStateFlow()

    var totalBalance by mutableStateOf(0.0)
        private set

    var totalIncome by mutableStateOf(0.0)
        private set

    var totalExpense by mutableStateOf(0.0)
        private set

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            initializeDefaultCategories()
        }
    }

    private suspend fun initializeDefaultCategories() {
        val defaultCategories = listOf(
            Triple("Shopping", "shopping_cart", true),
            Triple("Food", "restaurant", true),
            Triple("Transport", "directions_car", true),
            Triple("Entertainment", "movie", false),
            Triple("Bills", "receipt", false),
            Triple("Health", "favorite", false),
            Triple("Education", "school", false),
            Triple("Gifts", "card_giftcard", false)
        )

        defaultCategories.forEach { (name, icon, isFavorite) ->
            databaseHelper.insertCategory(name, icon, isFavorite)
        }
    }

    suspend fun loadExpenses() {
        databaseHelper.getAllExpenses().collect { expenses ->
            _expenses.value = expenses
            updateBalance(expenses)
        }
    }

    suspend fun loadCategories() {
        databaseHelper.getAllCategories().collect { categories ->
            _categories.value = categories
        }
    }

    suspend fun loadFavoriteCategories() {
        databaseHelper.getFavoriteCategories().collect { categories ->
            _favoriteCategories.value = categories
        }
    }

    suspend fun addExpense(
        title: String,
        amount: Double,
        category: String,
        type: String,
        tax: Double
    ) {
        databaseHelper.insertExpense(title, amount, category, type, tax)
        loadExpenses()
    }

    suspend fun toggleCategoryFavorite(category: CategoryModel) {
        databaseHelper.updateCategoryFavorite(category.id, !category.isFavorite)
        loadCategories()
        loadFavoriteCategories()
    }

    private fun updateBalance(expenses: List<ExpenseModel>) {
        totalIncome = expenses
            .filter { it.type == "income" }
            .sumOf { it.amount }

        totalExpense = expenses
            .filter { it.type == "expense" }
            .sumOf { it.amount }

        totalBalance = totalIncome - totalExpense
    }
} 