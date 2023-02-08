package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.UserService
import com.bracketcove.authorization.LogInResult
import com.bracketcove.authorization.SignUpResult
import com.bracketcove.domain.User
import com.bracketcove.domain.UserStatus
import com.bracketcove.domain.UserType

class FakeUserService : UserService {
    override suspend fun attemptSignUp(
        phoneNumber: String,
        userName: String
    ): ServiceResult<SignUpResult> {
        return ServiceResult.Success(SignUpResult.SUCCESS)
    }

    override suspend fun attemptLogin(phoneNumber: String): ServiceResult<LogInResult> {
        return ServiceResult.Success(LogInResult.SUCCESS)
    }

    override suspend fun getUser(): ServiceResult<User?> {
        return ServiceResult.Success(testUser)
    }

    override suspend fun getUserById(userId: String): ServiceResult<User?> {
        return ServiceResult.Success(testUser.copy(type = UserType.DRIVER.value,
            latitude = 51.0443,
            longitude = -113.06,
        ))
    }

    override suspend fun attemptLogout(): ServiceResult<Unit> {
        return ServiceResult.Success(Unit)
    }

    override suspend fun updateUser(user: User): ServiceResult<User?> {
        return ServiceResult.Success(testUser)
    }

    override suspend fun attemptUserAvatarUpdate(user: User, uri: String): ServiceResult<String?> {
        return ServiceResult.Success(uri)
    }

    override suspend fun attemptVehicleAvatarUpdate(user: User, url: String): ServiceResult<String?> {
        return ServiceResult.Success(url)
    }

    override suspend fun getPassengersLookingForRide(): ServiceResult<List<User>?> {
       // return ServiceResult.Success(listOf(testUser))
        return ServiceResult.Success(emptyList())

    }
}

private val testUser = User(
    "123456",
    "Saitama",
    UserType.DRIVER.value,
    UserStatus.INACTIVE.value,
    "https://static.wikia.nocookie.net/onepunchman/images/9/9b/Saitama_regular_face.jpg/revision/latest?cb=20200316015620",
    "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1d/Wild_Burros.jpg/1280px-Wild_Burros.jpg",
    "We ride tandem on a Donkey.",
    false,
    51.0443,
    -114.06,
    "Some time before",
    "Some time after"
)