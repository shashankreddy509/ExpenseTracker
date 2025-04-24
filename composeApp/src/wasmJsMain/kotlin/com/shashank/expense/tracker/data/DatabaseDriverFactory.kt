package data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import com.shashank.expense.tracker.db.ExpenseDatabase
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(js("new URL('', import.meta.url)") as String),
            ExpenseDatabase.Schema
        )
    }
} 