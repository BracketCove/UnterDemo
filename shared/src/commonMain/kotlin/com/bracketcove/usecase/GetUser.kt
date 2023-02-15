package com.bracketcove.usecase

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser

class GetUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun getUser(): ServiceResult<UnterUser?> {
        val getSession = authService.getSession()
        return when (getSession) {
            is ServiceResult.Failure -> getSession
            is ServiceResult.Value -> {
                if (getSession.value == null) getSession
                else getUserDetails()
            }
        }
    }

    private suspend fun getUserDetails(): ServiceResult<UnterUser?> {
        return userService.getUser().let { getDetailsResult ->
            when (getDetailsResult) {
                    is ServiceResult.Failure -> ServiceResult.Failure(getDetailsResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(getDetailsResult.value)
            }
        }
    }
}