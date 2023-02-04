package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.domain.User

class FakeAuthService : AuthService{
    override suspend fun attemptSignUp(phoneNumber: String, userName: String): ServiceResult<SignUpResult> {
        return ServiceResult.Success(SignUpResult.SUCCESS)
    }

    override suspend fun attemptLogin(phoneNumber: String): ServiceResult<LogInResult> {
        return ServiceResult.Success(LogInResult.INVALID_CREDENTIALS)
    }

    override suspend fun getUser(): ServiceResult<User?> {
        return ServiceResult.Success(null)
    }
}