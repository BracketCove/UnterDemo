package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.User

interface AuthService {
    suspend fun attemptSignUp(phoneNumber: String, userName: String): ServiceResult<SignUpResult>
    suspend fun attemptLogin(phoneNumber: String): ServiceResult<LogInResult>

    /**
     * A session is the period during which a user still has an authenticated connection to
     * the authorization services of the application. Sessions allow users to not have to
     * authenticate themselves every time they try to access a service.
     *
     * @return true if a session exists; else false
     */
    suspend fun getUser(): ServiceResult<User?>
}

enum class SignUpResult {
    SUCCESS,
    ALREADY_SIGNED_UP,
    INVALID_CREDENTIALS
}

enum class LogInResult {
    SUCCESS,
    INVALID_CREDENTIALS
}

