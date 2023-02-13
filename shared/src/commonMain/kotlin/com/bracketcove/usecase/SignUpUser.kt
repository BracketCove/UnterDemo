package com.bracketcove.usecase

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser

class SignUpUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun signUpUser(email: String, password: String, username: String): ServiceResult<SignUpResult> {
        val authAttempt = authService.signUp(email, password)

        return if (authAttempt is ServiceResult.Value) {
            when (authAttempt.value) {
                is SignUpResult.Success -> updateUserDetails(
                    email,
                    username,
                    authAttempt.value.uid
                )
                else -> authAttempt
            }
        } else authAttempt
    }


    private suspend fun updateUserDetails(
        email: String,
        username: String,
        uid: String
    ): ServiceResult<SignUpResult> {
        return userService.initializeNewUser(
            UnterUser(
                email = email,
                userId = uid,
                username = username
            )
        ).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(updateResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(SignUpResult.Success(uid))
            }
        }
    }
}