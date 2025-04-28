package com.shashank.expense.tracker.utils

import androidx.compose.ui.graphics.Color
import expensetracker.composeapp.generated.resources.Res
import expensetracker.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

object CategoryUtils {
    fun getCategoryIcon(category: String): DrawableResource {
        return when (category.lowercase()) {
            "shopping" -> Res.drawable.ic_shopping
            "subscription" -> Res.drawable.ic_recurring_bill
            "food" -> Res.drawable.ic_restaurant
            "salary" -> Res.drawable.ic_salary
            "transportation" -> Res.drawable.ic_car
            "bills" -> Res.drawable.ic_recurring_bill
            "income" -> Res.drawable.ic_income
            "expense" -> Res.drawable.ic_expense
            else -> Res.drawable.ic_camera
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category.lowercase()) {
            "shopping" -> Color(0xFF7B61FF)
            "subscription" -> Color(0xFFFE774C)
            "food" -> Color(0xFF00B2FF)
            "salary" -> Color(0xFF00C48C)
            "transportation" -> Color(0xFFFFB800)
            "entertainment" -> Color(0xFFFF6B6B)
            "health" -> Color(0xFF4CAF50)
            "education" -> Color(0xFF9C27B0)
            "bills" -> Color(0xFFE91E63)
            "income" -> Color(0xFF00C48C)
            "expense" -> Color(0xFFFF6B6B)
            else -> Color(0xFF607D8B)
        }
    }
} 