package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser
import io.getstream.chat.android.client.ChatClient

class StreamUserService(
    private val client: ChatClient
) : UserService {
    override suspend fun getUser(): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun attemptUserAvatarUpdate(user: UnterUser, url: String): ServiceResult<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun attemptVehicleAvatarUpdate(
        user: UnterUser,
        url: String
    ): ServiceResult<String?> {
        TODO("Not yet implemented")
    }

    override suspend fun getPassengersLookingForRide(): ServiceResult<List<UnterUser>?> {
        TODO("Not yet implemented")
    }
}