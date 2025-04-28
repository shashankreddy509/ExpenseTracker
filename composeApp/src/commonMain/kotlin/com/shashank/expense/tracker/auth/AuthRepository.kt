package com.shashank.expense.tracker.auth

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Flow<AuthState>
    suspend fun signUp(email: String, password: String): Flow<AuthState>
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}

class FirebaseAuthRepository(private val auth: FirebaseAuth) : AuthRepository {
    override suspend fun signIn(email: String, password: String): Flow<AuthState> = flow {
        emit(AuthState.Loading)
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
            result.user?.let { user ->
                emit(AuthState.Authenticated(user.uid))
            } ?: emit(AuthState.Error("Sign in failed"))
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: "Unknown error occurred"))
        } finally {

        }
    }

    override suspend fun signUp(email: String, password: String): Flow<AuthState> = flow {
        emit(AuthState.Loading)
        try {
            val result = auth.createUserWithEmailAndPassword(email, password)
            result.user?.let { user ->
                emit(AuthState.Authenticated(user.uid))
            } ?: emit(AuthState.Error("Sign up failed"))
        } catch (e: Exception) {
            emit(AuthState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
} 