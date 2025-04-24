package com.shashank.expense.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.shashank.expense.tracker.common.App
import com.shashank.expense.tracker.database.createDatabaseDriverFactory
import data.DatabaseHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val driverFactory = createDatabaseDriverFactory()
            val driver = remember { driverFactory.createDriver() }
            val databaseHelper = remember { DatabaseHelper(driver) }
            App(databaseHelper = databaseHelper)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Preview not available for database-dependent UI
}