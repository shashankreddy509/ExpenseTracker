package com.shashank.expense.tracker.di

import app.cash.sqldelight.db.SqlDriver
import com.shashank.expense.tracker.database.provideSqlDriver
import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            module {
                single<SqlDriver> { provideSqlDriver() }
                single<DatabaseHelper> { DatabaseHelper(get()) }
                single<HomeViewModel> { HomeViewModel(get()) }
            }
        )
    }
} 