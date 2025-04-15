package data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import com.shashank.expense.tracker.db.ExpenseDatabase
import com.shashank.expense.tracker.db.Expense
import com.shashank.expense.tracker.db.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class DatabaseHelper(sqlDriver: SqlDriver) {
    private val database = ExpenseDatabase(sqlDriver)
    private val queries = database.expenseDatabaseQueries
    
    // Domain Models
    data class ExpenseModel(
        val id: Long,
        val title: String,
        val amount: Double,
        val category: String,
        val type: String,
        val tax: Double,
        val date: Long,
        val createdAt: Long
    )
    
    data class CategoryModel(
        val id: Long,
        val name: String,
        val icon: String,
        val isFavorite: Boolean
    )
    
    // Expense Operations
    fun getAllExpenses(): Flow<List<ExpenseModel>> =
        queries
            .getAllExpenses()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { expenses: List<Expense> ->
                expenses.map { expense ->
                    ExpenseModel(
                        id = expense.id,
                        title = expense.title,
                        amount = expense.amount,
                        category = expense.category,
                        type = expense.type,
                        tax = expense.tax,
                        date = expense.date,
                        createdAt = expense.created_at
                    )
                }
            }
    
    fun getRecentExpenses(limit: Long): Flow<List<ExpenseModel>> =
        queries
            .getRecentExpenses(limit)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { expenses: List<Expense> ->
                expenses.map { expense ->
                    ExpenseModel(
                        id = expense.id,
                        title = expense.title,
                        amount = expense.amount,
                        category = expense.category,
                        type = expense.type,
                        tax = expense.tax,
                        date = expense.date,
                        createdAt = expense.created_at
                    )
                }
            }
    
    suspend fun insertExpense(
        title: String,
        amount: Double,
        category: String,
        type: String,
        tax: Double
    ) {
        queries.insertExpense(
            title = title,
            amount = amount,
            category = category,
            type = type,
            tax = tax,
            date = Clock.System.now().toEpochMilliseconds()
        )
    }
    
    // Category Operations
    fun getAllCategories(): Flow<List<CategoryModel>> =
        queries
            .getAllCategories()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories: List<Category> ->
                categories.map { category ->
                    CategoryModel(
                        id = category.id,
                        name = category.name,
                        icon = category.icon,
                        isFavorite = category.is_favorite == 1L
                    )
                }
            }
    
    fun getFavoriteCategories(): Flow<List<CategoryModel>> =
        queries
            .getFavoriteCategories()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { categories: List<Category> ->
                categories.map { category ->
                    CategoryModel(
                        id = category.id,
                        name = category.name,
                        icon = category.icon,
                        isFavorite = category.is_favorite == 1L
                    )
                }
            }
    
    suspend fun insertCategory(
        name: String,
        icon: String,
        isFavorite: Boolean = false
    ) {
        queries.insertCategory(
            name = name,
            icon = icon,
            is_favorite = if (isFavorite) 1L else 0L
        )
    }
    
    suspend fun updateCategoryFavorite(id: Long, isFavorite: Boolean) {
        queries.updateCategoryFavorite(
            id = id,
            isFavorite = if (isFavorite) 1L else 0L
        )
    }
} 