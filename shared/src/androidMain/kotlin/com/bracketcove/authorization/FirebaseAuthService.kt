package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthService(
    val auth: FirebaseAuth
) : AuthorizationService {
    override suspend fun signUp(
        email: String,
        password: String
    ): ServiceResult<SignUpResult> = withContext(Dispatchers.IO) {
        try {
            val authAttempt = auth.createUserWithEmailAndPassword(email, password).await()
            if (authAttempt.user != null) ServiceResult.Value(
                SignUpResult.Success(authAttempt.user!!.uid)
            )
            else ServiceResult.Failure(Exception("Null user"))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthWeakPasswordException -> ServiceResult.Value(SignUpResult.InvalidCredentials)
                is FirebaseAuthInvalidCredentialsException -> ServiceResult.Value(SignUpResult.InvalidCredentials)
                is FirebaseAuthUserCollisionException -> ServiceResult.Value(SignUpResult.AlreadySignedUp)
                else -> ServiceResult.Failure(exception)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): ServiceResult<LogInResult> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }
}