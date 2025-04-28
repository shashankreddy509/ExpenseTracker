package com.shashank.expense.tracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shashank.expense.tracker.models.CategoryModel
import com.shashank.expense.tracker.models.ExpenseModel
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel
import com.shashank.expense.tracker.utils.DateTimeUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant

@Composable
fun AddExpenseScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var isIncome by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.headlineMedium
        )
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Category Selection
        categories.forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedCategory?.id == category.id,
                    onClick = { selectedCategory = category }
                )
                Text(
                    text = category.title,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        
        // Income/Expense Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isIncome,
                onClick = { isIncome = true }
            )
            Text(
                text = "Income",
                modifier = Modifier.padding(start = 8.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            RadioButton(
                selected = !isIncome,
                onClick = { isIncome = false }
            )
            Text(
                text = "Expense",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Button(
            onClick = {
                if (title.isNotBlank() && amount.isNotBlank() && selectedCategory != null) {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val timestamp = now.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    val expense = ExpenseModel(
                        id = 0,
                        title = title,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = selectedCategory!!.title,
                        type = if (isIncome) "income" else "expense",
                        tax = 0.0,
                        date = timestamp,
                        createdAt = timestamp
                    )
                    viewModel.addExpense(expense)
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add")
        }
    }
} 