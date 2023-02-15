package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.constants.*
import com.bracketcove.domain.UnterUser
import com.bracketcove.domain.UserType
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.extensions.state
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class StreamUserService(
    private val client: ChatClient
) : UserService {


    //attempt to get the current user from the client. If that fails, make a new connection
    //and add a delay
    override suspend fun getUser(userId: String): ServiceResult<UnterUser?> {
        val user = client.getCurrentUser()
        Log.d("GET_USER", "Connecting: ${client.clientState.isConnecting}")
        Log.d("GET_USER", "Initialized: ${client.clientState.isInitialized}")
        Log.d("GET_USER", "Network Available: ${client.clientState.isNetworkAvailable}")
        Log.d("GET_USER", "Offline: ${client.clientState.isOffline}")

        return if (user == null) {
            Log.d("GET_USER", client.clientState.user.value.toString())
            ServiceResult.Value(null)
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
            val currentUser = client.getCurrentUser()
            if (currentUser != null && currentUser.id == userId) {
                val extraData = currentUser.extraData
                val type: String? = extraData[KEY_TYPE] as String?
                val status: String? = extraData[KEY_STATUS] as String?

                ServiceResult.Value(
                    UnterUser(
                        userId = userId,
                        username = currentUser.name,
                        avatarPhotoUrl = currentUser.image,
                        createdAt = currentUser.createdAt.toString(),
                        updatedAt = currentUser.updatedAt.toString(),
                        status = status ?: "",
                        type = type ?: ""
                    )
                )
            } else if (currentUser != null && currentUser.id != userId){
                val streamUser = User(
                    id = userId
                )

                val devToken = client.devToken(userId)
                val getUserResult = client.switchUser(streamUser, devToken).await()

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
            } else {
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
            disconnectUser(user.userId)

            delay(4000L)
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

            val devToken = client.devToken(user.userId)
            val result = client.connectUser(streamUser, devToken).await()

            if (result.isSuccess) {
                ServiceResult.Value(
                    user
                )
            } else {
                ServiceResult.Failure(Exception(result.error().cause))
            }
        }

    private suspend fun disconnectUser(userId: String) {
        val currentUser = client.getCurrentUser()
        if (currentUser != null && userId == currentUser.id) {
            client.disconnect(false).await()
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