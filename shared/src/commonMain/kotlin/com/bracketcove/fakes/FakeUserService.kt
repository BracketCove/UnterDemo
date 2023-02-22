package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser

class FakeUserService : UserService {
    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> {
        return ServiceResult.Value(UnterUser())
    }

    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> {
        return ServiceResult.Value(null)
    }

    override suspend fun initializeNewUser(user: UnterUser): ServiceResult<UnterUser?> {
        return ServiceResult.Value(null)
    }

    override suspend fun logOutUser(user: UnterUser) {
        return Unit
    }
}