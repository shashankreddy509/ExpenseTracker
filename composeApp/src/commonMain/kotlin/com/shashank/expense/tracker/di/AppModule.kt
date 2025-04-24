package com.shashank.expense.tracker.di

import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.viewmodels.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { DatabaseHelper(get()) }
    single { HomeViewModel(get()) }
}

fun initKoin(block: org.koin.core.KoinApplication.() -> Unit = {}) {
    startKoin {
        modules(appModule)
        block()
    }
} 