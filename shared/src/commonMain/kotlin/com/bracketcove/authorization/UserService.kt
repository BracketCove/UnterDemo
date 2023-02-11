package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser

interface UserService {


    /**
     * A session is the period during which a user still has an authenticated connection to
     * the authorization services of the application. Sessions allow users to not have to
     * authenticate themselves every time they try to access a service.
     *
     * @return true if a session exists; else false
     */
    suspend fun getUser(): ServiceResult<UnterUser?>

    suspend fun getUserById(userId: String): ServiceResult<UnterUser?>
    suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?>

    suspend fun getPassengersLookingForRide(): ServiceResult<List<UnterUser>?>

    suspend fun logOutUser(user: UnterUser): Unit
}