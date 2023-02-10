package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.enqueue
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreamUserService(
    private val client: ChatClient
) : UserService {
    override suspend fun getUser(): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> =
        withContext(Dispatchers.IO) {
            val streamUser = user.let {
                User(
                    id = user.userId,
                    extraData = mutableMapOf(
                        "username" to user.username,
                        "type" to user.type
                    )
                )
            }
            val token = client.devToken(user.userId)
            val result = client.connectUser(streamUser, token).await()

            if (result.isSuccess) {
                ServiceResult.Value(user)
            } else {
                Log.d(
                    "STREAM_API",
                    result.error().message ?: "Stream error occurred for update user"
                )
                ServiceResult.Failure(Exception(result.error().cause))
            }
        }

    override suspend fun attemptUserAvatarUpdate(
        user: UnterUser,
        url: String
    ): ServiceResult<String?> {
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