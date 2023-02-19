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
                else getUserDetails(getSession.value.userId)
            }
        }
    }

    private suspend fun getUserDetails(uid: String): ServiceResult<UnterUser?> {
        return userService.getUserById(uid).let { getDetailsResult ->
            when (getDetailsResult) {
                    is ServiceResult.Failure -> ServiceResult.Failure(getDetailsResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(getDetailsResult.value)
            }
        }
    }
}