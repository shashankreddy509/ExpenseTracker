package com.shashank.expense.tracker.common

import androidx.compose.runtime.Composable
import com.shashank.expense.tracker.screens.HomeScreen
import com.shashank.expense.tracker.viewmodels.HomeViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val viewModel: HomeViewModel = koinInject()
    HomeScreen(viewModel = viewModel)
}

private sealed class Screen {
    object Home : Screen()
    object AddExpense : Screen()
} 