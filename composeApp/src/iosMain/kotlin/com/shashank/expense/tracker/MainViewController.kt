package com.shashank.expense.tracker

import androidx.compose.ui.window.ComposeUIViewController
import com.shashank.expense.tracker.common.App
import com.shashank.expense.tracker.di.initKoin

fun MainViewController() = ComposeUIViewController { 
    initKoin()
    App() 
}