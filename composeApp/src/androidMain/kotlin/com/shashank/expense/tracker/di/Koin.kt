package com.shashank.expense.tracker.di

import app.cash.sqldelight.db.SqlDriver
import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.database.provideSqlDriver
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel
import org.koin.dsl.module

val androidModule = module {
    single<SqlDriver> { provideSqlDriver() }
    single<DatabaseHelper> { DatabaseHelper(get()) }
    single<HomeViewModel> { HomeViewModel(get()) }
}

