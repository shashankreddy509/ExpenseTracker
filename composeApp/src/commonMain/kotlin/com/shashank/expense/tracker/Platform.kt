package com.shashank.expense.tracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform