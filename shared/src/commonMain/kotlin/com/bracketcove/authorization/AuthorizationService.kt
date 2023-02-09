package com.bracketcove.authorization

import com.bracketcove.ServiceResult

interface AuthorizationService {
    /**
     * @return uid if sign up is successful
     */
    suspend fun signUp(email: String, password: String): ServiceResult<SignUpResult>
    suspend fun login(email: String, password: String): ServiceResult<LogInResult>

    suspend fun logout(): ServiceResult<Unit>

}