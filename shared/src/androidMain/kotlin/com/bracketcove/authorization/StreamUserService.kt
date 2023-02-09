package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.User
import io.getstream.chat.android.client.ChatClient

class StreamUserService(
    private val client: ChatClient
) : UserService {
    override suspend fun getUser(): ServiceResult<User?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): ServiceResult<User?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): ServiceResult<User?> {
        TODO("Not yet implemented")
    }

    override suspend fun attemptUserAvatarUpdate(user: User, url: String): ServiceResult<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun attemptVehicleAvatarUpdate(
        user: User,
        url: String
    ): ServiceResult<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun getPassengersLookingForRide(): ServiceResult<List<User>?> {
        TODO("Not yet implemented")
    }
}