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

    suspend fun attemptUserAvatarUpdate(user: UnterUser, url: String): ServiceResult<String?>

    suspend fun attemptVehicleAvatarUpdate(user: UnterUser, url: String): ServiceResult<String?>
    suspend fun getPassengersLookingForRide(): ServiceResult<List<UnterUser>?>
}

sealed interface SignUpResult {
    data class Success(val uid: String) : SignUpResult
    object AlreadySignedUp : SignUpResult
    object InvalidCredentials : SignUpResult
}

enum class LogInResult {
    SUCCESS,
    INVALID_CREDENTIALS
}


