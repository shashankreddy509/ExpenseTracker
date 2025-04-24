package com.shashank.expense.tracker.database

import app.cash.sqldelight.db.SqlDriver
import com.shashank.expense.tracker.data.DatabaseDriverFactory

actual fun provideSqlDriver(): SqlDriver {
    return DatabaseDriverFactory().createDriver()
} 