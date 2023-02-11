package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.constants.*
import com.bracketcove.domain.UnterUser
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.enqueue
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
            val lat: Double? = extraData[KEY_LAT] as Double?
            val lon: Double? = extraData[KEY_LON] as Double?
            val type: String? = extraData[KEY_TYPE] as String?
            val address: String? = extraData[KEY_ADDRESS] as String?
            val status: String? = extraData[KEY_STATUS] as String?

            ServiceResult.Value(
                UnterUser(
                    userId = user.id,
                    username = user.name,
                    avatarPhotoUrl = user.image,
                    createdAt = user.createdAt.toString(),
                    updatedAt = user.updatedAt.toString(),
                    latitude = lat ?: 999.0,
                    longitude = lon ?: 999.0,
                    type = type ?: "",
                    address = address ?: "",
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
                val lat: Double? = extraData[KEY_LAT] as Double?
                val lon: Double? = extraData[KEY_LON] as Double?
                val type: String? = extraData[KEY_TYPE] as String?
                val address: String? = extraData[KEY_ADDRESS] as String?
                val status: String? = extraData[KEY_STATUS] as String?

                ServiceResult.Value(
                    UnterUser(
                        userId = userId,
                        username = user.name,
                        avatarPhotoUrl = user.image,
                        createdAt = user.createdAt.toString(),
                        updatedAt = user.updatedAt.toString(),
                        latitude = lat ?: 999.0,
                        longitude = lon ?: 999.0,
                        type = type ?: "",
                        address = address ?: "",
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
                        KEY_LAT to user.latitude,
                        KEY_LON to user.longitude,
                        KEY_TYPE to user.type,
                        KEY_ADDRESS to user.address,
                        KEY_STATUS to user.status
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