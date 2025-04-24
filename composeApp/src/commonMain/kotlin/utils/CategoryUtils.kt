package utils

import androidx.compose.ui.graphics.Color
import expensetracker.composeapp.generated.resources.Res
import expensetracker.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import screens.CategoryModel

object CategoryUtils {
    fun getCategoryIcon(category: CategoryModel): DrawableResource {
        return when (category.title.lowercase()) {
            "shopping" -> Res.drawable.ic_shopping
            "subscription" -> Res.drawable.ic_recurring_bill
            "food" -> Res.drawable.ic_restaurant
            "salary" -> Res.drawable.ic_salary
            "transportation" -> Res.drawable.ic_car
//            "entertainment" -> Res.drawable.ic_entertainment
//            "health" -> Res.drawable.ic_health
//            "education" -> Res.drawable.ic_education
//            "others" -> Res.drawable.ic_others
            "bills" -> Res.drawable.ic_recurring_bill
            else -> Res.drawable.ic_camera
        }
    }

    fun getCategoryColor(category: CategoryModel): Color {
        return when (category.title.lowercase()) {
            "shopping" -> Color(0xFF7B61FF)
            "subscription" -> Color(0xFFFE774C)
            "food" -> Color(0xFF00B2FF)
            "salary" -> Color(0xFF00C48C)
            "transportation" -> Color(0xFFFFB800)
            "entertainment" -> Color(0xFFFF6B6B)
            "health" -> Color(0xFF4CAF50)
            "education" -> Color(0xFF9C27B0)
            "bills" -> Color(0xFFE91E63)
            "others" -> Color(0xFF607D8B)
            else -> Color(0xFF607D8B)
        }
    }
} 