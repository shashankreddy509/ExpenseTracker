package di

import data.DatabaseHelper
import org.koin.core.module.Module
import org.koin.dsl.module
import viewmodels.HomeViewModel

object AppModule {
    val appModule = module {
        single { DatabaseHelper(get()) }
        single { HomeViewModel(get()) }
    }
} 