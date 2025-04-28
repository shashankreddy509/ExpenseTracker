package com.shashank.expense.tracker

import android.app.Application
import com.shashank.expense.tracker.di.initializeKoin
import org.koin.android.ext.koin.androidContext

class TrackerApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin {
            androidContext(this@TrackerApplication)
        }
    }
}