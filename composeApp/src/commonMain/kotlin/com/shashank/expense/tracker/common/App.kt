package com.shashank.expense.tracker.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.shashank.expense.tracker.database.createDatabaseDriverFactory
import data.DatabaseHelper
import screens.AddExpenseScreen
import screens.HomeScreen
import viewmodels.HomeViewModel

@Composable
fun App(
    databaseHelper: DatabaseHelper
) {
    MaterialTheme {
        val viewModel = remember { HomeViewModel(databaseHelper) }
        
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(viewModel = viewModel)
        }
    }
}

private sealed class Screen {
    object Home : Screen()
    object AddExpense : Screen()
} 