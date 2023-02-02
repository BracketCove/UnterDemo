package com.bracketcove.authorization

import com.bracketcove.ServiceResult

interface AuthService {
    suspend fun attemptSignUp(phoneNumber: String): ServiceResult<SignUpResult>
    suspend fun attemptLogin(phoneNumber: String): ServiceResult<SignUpResult>
}

enum class SignUpResult {
    SUCCESS,
    ALREADY_SIGN_UP
}