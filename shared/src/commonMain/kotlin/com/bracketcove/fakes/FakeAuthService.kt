package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.domain.UnterUser

class FakeAuthService : AuthorizationService {
    override suspend fun signUp(email: String, password: String): ServiceResult<SignUpResult> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): ServiceResult<LogInResult> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getSession(): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

}