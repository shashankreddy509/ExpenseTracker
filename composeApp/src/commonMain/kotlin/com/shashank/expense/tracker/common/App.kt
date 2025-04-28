package com.shashank.expense.tracker.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.shashank.expense.tracker.auth.AuthState
import com.shashank.expense.tracker.auth.AuthViewModel
import com.shashank.expense.tracker.navigation.Screen
import com.shashank.expense.tracker.screens.homescreen.HomeScreen
import com.shashank.expense.tracker.screens.auth.LoginScreen
import com.shashank.expense.tracker.screens.onboarding.OnboardingScreen
import com.shashank.expense.tracker.screens.auth.SignUpScreen
import org.koin.compose.koinInject
import com.shashank.expense.tracker.screens.homescreen.HomeViewModel

@Composable
fun App() {
    // Initialize Koin only once
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Onboarding) }
    var lastOnboardingPageShown by remember { mutableStateOf(false) }
    val authViewModel = koinInject<AuthViewModel>()

    // Observe auth state changes
    LaunchedEffect(authViewModel) {
        authViewModel.authState.collect { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    currentScreen = Screen.Home
                }
                is AuthState.Unauthenticated -> {
                    if (lastOnboardingPageShown) {
                        currentScreen = Screen.Login
                    }
                }
                else -> {}
            }
        }
    }

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
                viewModel = authViewModel,
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
                viewModel = authViewModel,
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