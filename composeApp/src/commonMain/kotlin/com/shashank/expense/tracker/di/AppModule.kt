package com.shashank.expense.tracker.di

import app.cash.sqldelight.db.SqlDriver
import com.shashank.expense.tracker.auth.AuthRepository
import com.shashank.expense.tracker.auth.AuthViewModel
import com.shashank.expense.tracker.auth.FirebaseAuthRepository
import com.shashank.expense.tracker.data.DatabaseHelper
import com.shashank.expense.tracker.database.provideSqlDriver
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

val commonModule = module {
    single<SqlDriver> { provideSqlDriver() }
    single<DatabaseHelper> { DatabaseHelper(get()) }
    single<HomeViewModel> { HomeViewModel(get()) }
    single { Firebase.auth }
    single<AuthRepository> { FirebaseAuthRepository(get()) }
    single { AuthViewModel(get()) }

}

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(commonModule)
    }
}