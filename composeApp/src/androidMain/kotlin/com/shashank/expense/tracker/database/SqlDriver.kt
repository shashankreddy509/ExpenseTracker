package com.shashank.expense.tracker.database

import android.app.Activity
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shashank.expense.tracker.db.ExpenseDatabase

actual fun provideSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(
        schema = ExpenseDatabase.Schema,
        context = Activity(),
        name = "expense.db"
    )
} 