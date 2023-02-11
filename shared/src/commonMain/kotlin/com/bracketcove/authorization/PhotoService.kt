package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.domain.UnterUser

interface PhotoService {
    suspend fun attemptUserAvatarUpdate(url: String): ServiceResult<String>
}