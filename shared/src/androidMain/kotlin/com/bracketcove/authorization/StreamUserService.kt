package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.constants.*
import com.bracketcove.domain.UnterUser
import com.bracketcove.domain.UserType
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class StreamUserService(
    private val client: ChatClient
) : UserService {

    override suspend fun getUser(): ServiceResult<UnterUser?> = withContext(Dispatchers.IO) {
        val user = client.getCurrentUser()
        if (user == null) {
            client.disconnect(true).await()
            ServiceResult.Value(user)
        } else {
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

    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> =
        withContext(Dispatchers.IO) {
            val streamUser = User(
                id = userId
            )
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
                        status = status ?: "",
                        type = type ?: ""
                    )
                )
            } else {
                Log.d(
                    "GET_USER_BY_ID",
                    getUserResult.error().message ?: "Stream error occurred for update user"
                )
                ServiceResult.Failure(Exception(getUserResult.error().message))
            }
        }


    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> =
        withContext(Dispatchers.IO) {
            val result = client.updateUser(
                user.let {
                    User(
                        id = user.userId,
                        image = user.avatarPhotoUrl,
                        name = user.username,
                        extraData = mutableMapOf(
                            KEY_STATUS to user.status,
                            KEY_TYPE to user.type
                        )
                    )
                }
            ).await()

            if (result.isSuccess) {
                ServiceResult.Value(user)
            } else {
                Log.d(
                    "UPDATE_USER",
                    result.error().message ?: "Stream error occurred for update user"
                )
                ServiceResult.Failure(Exception(result.error().cause))
            }
        }

    override suspend fun initializeNewUser(user: UnterUser): ServiceResult<UnterUser?> =
        withContext(Dispatchers.IO) {
            disconnectUser(user)

            val streamUser = user.let {
                User(
                    id = user.userId,
                    name = user.username,
                    extraData = mutableMapOf(
                        KEY_STATUS to user.status,
                        KEY_TYPE to user.type
                    )
                )
            }

            val token = client.devToken(user.userId)
            val result = client.connectUser(streamUser, token).await()

            if (result.isSuccess) {
                ServiceResult.Value(
                    user
                )
            } else {
                ServiceResult.Failure(Exception(result.error().cause))
            }
        }

    private fun disconnectUser(user: UnterUser) {
        val currentUser = client.getCurrentUser()
        if (currentUser != null && user.userId == currentUser.id) {
            client.disconnect(false).enqueue()
        }
    }

    override suspend fun logOutUser(user: UnterUser) =
        withContext(Dispatchers.IO) {
            val result = client.disconnect(flushPersistence = true).await()
            if (result.isError) Log.d(
                "LOG_USER_OUT",
                result.error().message ?: "Error logging out"
            )
            Unit
        }
}