package di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shashank.expense.tracker.db.ExpenseDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun createAndroidModule(context: Context) = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = ExpenseDatabase.Schema,
            context = context,
            name = "expense.db"
        )
    }
    
    single<ExpenseDatabase> {
        ExpenseDatabase(get())
    }
} 