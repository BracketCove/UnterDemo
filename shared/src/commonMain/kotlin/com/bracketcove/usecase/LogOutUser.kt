package com.bracketcove.usecase

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.AuthorizationService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser

class LogOutUser(
    val authService: AuthorizationService,
    val userService: UserService
) {

    suspend fun logout(user: UnterUser): ServiceResult<Unit> {
        authService.logout()
        userService.logOutUser(user)

        return ServiceResult.Value(Unit)
    }
}