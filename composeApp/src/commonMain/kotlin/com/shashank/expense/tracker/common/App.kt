package com.shashank.expense.tracker.common

import HomeScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shashank.expense.tracker.database.createDatabaseDriverFactory
import data.DatabaseHelper
import viewmodels.HomeViewModel

@Composable
fun App() {
    val driverFactory = createDatabaseDriverFactory()
    val driver = remember { driverFactory.createDriver() }
    val databaseHelper = remember { DatabaseHelper(driver) }
    val viewModel = remember { HomeViewModel(databaseHelper) }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(viewModel = viewModel)
        }
    }
} 