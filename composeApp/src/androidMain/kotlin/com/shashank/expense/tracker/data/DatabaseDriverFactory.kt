package com.shashank.expense.tracker.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shashank.expense.tracker.db.ExpenseDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ExpenseDatabase.Schema,
            context = context,
            name = "expense.db"
        )
    }
}