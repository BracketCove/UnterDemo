package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.domain.UnterUser

class FakeAuthorizationService : AuthorizationService {
    override suspend fun signUp(email: String, password: String): ServiceResult<SignUpResult> {
        return ServiceResult.Value(SignUpResult.AlreadySignedUp)
    }

    override suspend fun login(email: String, password: String): ServiceResult<LogInResult> {
        return ServiceResult.Value(LogInResult.Success(UnterUser()))
    }

    override suspend fun logout(): ServiceResult<Unit> {
        return ServiceResult.Value(Unit)
    }

    override suspend fun getSession(): ServiceResult<UnterUser?> {
        return ServiceResult.Value(null)
    }
}