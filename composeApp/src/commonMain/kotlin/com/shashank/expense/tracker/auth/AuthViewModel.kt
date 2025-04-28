package com.shashank.expense.tracker.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dev.gitlive.firebase.auth.FirebaseUser

class AuthViewModel(private val authRepository: AuthRepository) : ScreenModel {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        authRepository.getCurrentUser()?.let { user: FirebaseUser ->
            _authState.value = AuthState.Authenticated(user.uid)
        }
    }

    fun signIn(email: String, password: String) {
        screenModelScope.launch {
            authRepository.signIn(email, password).collect { state ->
                _authState.value = state
            }
        }
    }

    fun signUp(email: String, password: String) {
        screenModelScope.launch {
            authRepository.signUp(email, password).collect { state ->
                _authState.value = state
            }
        }
    }

    fun signOut() {
        screenModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }
} 