package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.constants.KEY_STATUS
import com.bracketcove.constants.KEY_TYPE
import com.bracketcove.domain.UnterUser
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreamUserService(
    private val client: ChatClient
) : UserService {
    override suspend fun getUser(): ServiceResult<UnterUser?> = withContext(Dispatchers.IO) {
        val user = client.clientState.user.value
        if (user == null) ServiceResult.Value(user)
        else {
            val extraData = user.extraData
            val type: String? = extraData[KEY_TYPE] as String?
            val status: String? = extraData[KEY_STATUS] as String?

            ServiceResult.Value(
                UnterUser(
                    userId = user.id,
                    username = user.name,
                    avatarPhotoUrl = user.image,
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    type = type ?: "",
                    status = status ?: ""
                )
            )
        }
    }
    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> = withContext(Dispatchers.IO) {
        val streamUser = User(id = userId)
        val devToken = client.devToken(userId)

        val getUserResult = client.connectUser(streamUser, devToken).await()

        if (getUserResult.isSuccess) {
            val user = getUserResult.data().user
                val extraData = user.extraData
                val type: String? = extraData[KEY_TYPE] as String?
                val status: String? = extraData[KEY_STATUS] as String?

                ServiceResult.Value(
                    UnterUser(
                        userId = userId,
                        username = user.name,
                        avatarPhotoUrl = user.image,
                        createdAt = user.createdAt.toString(),
                        updatedAt = user.updatedAt.toString(),
                        status = status ?: ""
                    )
                )
        } else {
            ServiceResult.Failure(Exception(getUserResult.error().message))
        }
    }

    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> =
        withContext(Dispatchers.IO) {
            val streamUser = user.let {
                User(
                    id = user.userId,
                    name = user.username,
                    image = user.avatarPhotoUrl,
                    extraData = mutableMapOf(
                        KEY_TYPE to user.type,
                        KEY_STATUS to user.status
                    )
                )
            }

            val result = client.updateUser(streamUser).await()

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
    override suspend fun getPassengersLookingForRide(): ServiceResult<List<UnterUser>?> {
        TODO("Not yet implemented")
    }

    override suspend fun logOutUser(user: UnterUser) =
        withContext(Dispatchers.IO) {
        val result = client.disconnect(flushPersistence = true).await()
        if (result.isError) Log.d("STREAM_USER_SERVICE", result.error().message ?: "Error logging out")
        Unit
    }
}