package com.shashank.expense.tracker.database

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import data.DatabaseDriverFactory

@Composable
actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    val context = LocalContext.current
    return DatabaseDriverFactory(context)
} 