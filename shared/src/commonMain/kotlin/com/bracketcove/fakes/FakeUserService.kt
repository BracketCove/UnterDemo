package com.bracketcove.fakes

import com.bracketcove.ServiceResult
import com.bracketcove.authorization.UserService
import com.bracketcove.domain.UnterUser
import com.bracketcove.domain.UserStatus
import com.bracketcove.domain.UserType

class FakeUserService : UserService {
    override suspend fun getUser(): ServiceResult<UnterUser?> {
        return ServiceResult.Value(testUser.copy(type = UserType.PASSENGER.value))
    }

    override suspend fun getUserById(userId: String): ServiceResult<UnterUser?> {
        return ServiceResult.Value(testUser.copy(type = UserType.DRIVER.value,
            latitude = 51.0443,
            longitude = -113.06,
        ))
    }

    override suspend fun updateUser(user: UnterUser): ServiceResult<UnterUser?> {
        return ServiceResult.Value(testUser)
    }

    override suspend fun attemptUserAvatarUpdate(user: UnterUser, uri: String): ServiceResult<String?> {
        return ServiceResult.Value(uri)
    }

    override suspend fun attemptVehicleAvatarUpdate(user: UnterUser, url: String): ServiceResult<String?> {
        return ServiceResult.Value(url)
    }

    override suspend fun getPassengersLookingForRide(): ServiceResult<List<UnterUser>?> {
        return ServiceResult.Value(listOf(testUser.copy(
            latitude = 51.0543,
            longitude = -114.20,
        )))
        //return ServiceResult.Value(emptyList())

    }
}

private val testUser = UnterUser(
    "123456",
    "Saitama",
    "what@example.com",
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