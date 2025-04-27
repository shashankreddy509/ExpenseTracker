package com.shashank.expense.tracker.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shashank.expense.tracker.navigation.Screen
import com.shashank.expense.tracker.screens.homescreen.HomeScreen
import com.shashank.expense.tracker.screens.auth.LoginScreen
import com.shashank.expense.tracker.screens.onboarding.OnboardingScreen
import com.shashank.expense.tracker.screens.auth.SignUpScreen
import org.koin.compose.koinInject
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    var lastOnboardingPageShown by remember { mutableStateOf(false) }

    when (currentScreen) {
        Screen.Onboarding -> {
            OnboardingScreen(
                onNavigateToScreen = { screen ->
                    lastOnboardingPageShown = true
                    currentScreen = screen
                }
            )
        }
        Screen.Login -> {
            LoginScreen(
                onNavigateBack = {
                    currentScreen = Screen.Onboarding
                },
                onNavigateToScreen = { screen ->
                    currentScreen = screen
                }
            )
        }
        Screen.SignUp -> {
            SignUpScreen(
                onNavigateBack = {
                    currentScreen = Screen.Onboarding
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                },
                onSignUpSuccess = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.Home -> {
            val viewModel = koinInject<HomeViewModel>()
            HomeScreen(viewModel = viewModel)
        }
    }
} 