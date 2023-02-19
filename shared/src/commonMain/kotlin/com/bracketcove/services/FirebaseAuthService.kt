package com.bracketcove.services

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.domain.UnterUser
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FirebaseAuthService(
    val auth: FirebaseAuth
) : AuthorizationService {

    override suspend fun signUp(
        email: String,
        password: String
    ): ServiceResult<SignUpResult> = withContext(Dispatchers.Default) {
        try {
            val authAttempt = auth.createUserWithEmailAndPassword(email, password)

            if (authAttempt.user != null) ServiceResult.Value(
                SignUpResult.Success(authAttempt.user!!.uid)
            )
            else ServiceResult.Failure(Exception("Null user"))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidCredentialsException -> ServiceResult.Value(SignUpResult.InvalidCredentials)
                is FirebaseAuthUserCollisionException -> ServiceResult.Value(SignUpResult.AlreadySignedUp)
                else -> ServiceResult.Failure(exception)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): ServiceResult<LogInResult> = withContext(Dispatchers.Default) {
        try {
            val authAttempt = auth.signInWithEmailAndPassword(email, password)
            if (authAttempt.user != null) ServiceResult.Value(
                LogInResult.Success(
                    UnterUser(
                        userId = authAttempt.user!!.uid
                    )
                )
            )
            else ServiceResult.Failure(Exception("Null user"))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthInvalidUserException -> ServiceResult.Value(LogInResult.InvalidCredentials)
                is FirebaseAuthInvalidCredentialsException -> ServiceResult.Value(LogInResult.InvalidCredentials)
                else -> ServiceResult.Failure(exception)
            }
        }
    }

    override suspend fun logout(): ServiceResult<Unit> {
        auth.signOut()
        return ServiceResult.Value(Unit)
    }

    override suspend fun getSession(): ServiceResult<UnterUser?> {
//        logout()
//        return ServiceResult.Value(null)
        val firebaseUser = auth.currentUser
        return if (firebaseUser == null) ServiceResult.Value(null)
        else ServiceResult.Value(
            firebaseUser.let {
                UnterUser(
                    userId = it.uid
                )
            }
        )
    }
}