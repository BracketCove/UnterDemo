package com.bracketcove.usecase

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.PhotoService
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser

class UpdateUserAvatar(
    val photoService: PhotoService,
    val userService: UserService
) {
    suspend fun updateAvatar(user: UnterUser, uri: String): ServiceResult<String> {
        val updateAvatar = photoService.attemptUserAvatarUpdate(uri)
        return when (updateAvatar) {
            is ServiceResult.Failure -> updateAvatar
            is ServiceResult.Value -> updateUserPhoto(user, updateAvatar.value)
        }
    }

    private suspend fun updateUserPhoto(
        user: UnterUser,
        newUrl: String
    ): ServiceResult<String> {
        return userService.updateUser(
            user.copy(
                avatarPhotoUrl = newUrl
            )
        ).let { updateResult ->
            when (updateResult) {
                is ServiceResult.Failure -> ServiceResult.Failure(updateResult.exception)
                is ServiceResult.Value -> ServiceResult.Value(newUrl)
            }
        }
    }
}