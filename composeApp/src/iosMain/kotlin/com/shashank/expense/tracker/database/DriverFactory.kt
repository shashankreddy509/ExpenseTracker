package com.shashank.expense.tracker.database

import androidx.compose.runtime.Composable
import com.shashank.expense.tracker.data.DatabaseDriverFactory

@Composable
actual fun createDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
} 