package com.shashank.expense.tracker.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.shashank.expense.tracker.db.ExpenseDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply {
            ExpenseDatabase.Schema.create(this)
        }
    }
} 