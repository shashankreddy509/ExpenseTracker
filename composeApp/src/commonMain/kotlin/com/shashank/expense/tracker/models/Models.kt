package com.shashank.expense.tracker.models

data class CategoryModel(
    val id: Long,
    val title: String,
    val isFavorite: Boolean = false,
    val icon: String
)

data class ExpenseModel(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String,
    val tax: Double,
    val date: Long,
    val createdAt: Long
) {
    val isIncome: Boolean
        get() = type == "income"
} 