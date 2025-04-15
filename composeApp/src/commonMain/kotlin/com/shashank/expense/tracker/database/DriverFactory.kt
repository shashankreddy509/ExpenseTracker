package com.shashank.expense.tracker.database

import androidx.compose.runtime.Composable
import data.DatabaseDriverFactory

@Composable
expect fun createDatabaseDriverFactory(): DatabaseDriverFactory 