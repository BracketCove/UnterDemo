package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser

interface AuthorizationService {
    /**
     * @return uid if sign up is successful
     */
    suspend fun signUp(email: String, password: String): ServiceResult<SignUpResult>
    suspend fun login(email: String, password: String): ServiceResult<LogInResult>

    suspend fun logout(): ServiceResult<Unit>

    /**
     * @return true if a user session is active, else null
     */
    suspend fun getSession(): ServiceResult<UnterUser?>

}