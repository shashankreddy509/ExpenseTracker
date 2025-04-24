package com.shashank.expense.tracker.di

import com.shashank.expense.tracker.database.provideSqlDriver
import com.shashank.expense.tracker.db.ExpenseDatabase
import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.viewmodels.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            module {
                single<ExpenseDatabase> { ExpenseDatabase(provideSqlDriver()) }
                single<DatabaseHelper> { DatabaseHelper(provideSqlDriver()) }
                single<HomeViewModel> { HomeViewModel(get()) }
            }
        )
    }
} 