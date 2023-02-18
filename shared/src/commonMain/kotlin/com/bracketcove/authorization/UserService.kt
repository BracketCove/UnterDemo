package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser

interface UserService {

    suspend fun getUserById(userId: String): ServiceResult<UnterUser?>
    suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?>

    suspend fun initializeNewUser(user: UnterUser): ServiceResult<UnterUser?>

    suspend fun logOutUser(user: UnterUser): Unit
}