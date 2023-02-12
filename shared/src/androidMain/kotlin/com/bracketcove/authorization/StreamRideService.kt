package com.bracketcove.authorization

import com.bracketcove.ServiceResult
import com.bracketcove.constants.*
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.UnterUser
import com.bracketcove.rides.RideService
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.utils.onErrorSuspend
import io.getstream.chat.android.client.utils.onSuccessSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class StreamRideService(
    private val client: ChatClient
) : RideService {

    private val _rideModelUpdates: MutableStateFlow<ServiceResult<Ride?>> =
        MutableStateFlow(ServiceResult.Value(null))
    val rideModelUpdates: Flow<ServiceResult<Ride?>> = _rideModelUpdates
    override fun rideFlow(): Flow<ServiceResult<Ride?>> = rideModelUpdates

    override suspend fun getRideIfInProgress(): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.eq(STREAM_USER_ID, client.getCurrentUser()?.id ?: ""),
            ),
            querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
            limit = 1
        ).apply {
            state = true
        }

        val result = client.queryChannels(request).await()

        result.onSuccessSuspend { channels ->
            if (channels.isEmpty()) _rideModelUpdates.value = ServiceResult.Value(null)
            else {
                channels.first().let { channel ->
                    val extraData = channel.extraData
                    val destAddress: String? = extraData[KEY_DEST_ADDRESS] as String?
                    val destLat: Double? = extraData[KEY_DEST_LAT] as Double?
                    val destLon: Double? = extraData[KEY_DEST_LON] as Double?

                    val driverId: String? = extraData[KEY_DRIVER_ID] as String?
                    val driverLat: Double? = extraData[KEY_DRIVER_LAT] as Double?
                    val driverLon: Double? = extraData[KEY_DRIVER_LON] as Double?
                    val driverAvatar: String? = extraData[KEY_DRIVER_AVATAR_URL] as String?
                    val driverName: String? = extraData[KEY_DRIVER_NAME] as String?

                    val passengerId: String? = extraData[KEY_PASSENGER_ID] as String?
                    val passengerLat: Double? = extraData[KEY_PASSENGER_LAT] as Double?
                    val passengerLon: Double? = extraData[KEY_PASSENGER_LON] as Double?
                    val passengerAvatar: String? = extraData[KEY_PASSENGER_AVATAR_URL] as String?
                    val passengerName: String? = extraData[KEY_PASSENGER_NAME] as String?
                    val status: String? = extraData[KEY_STATUS] as String?

                    _rideModelUpdates.value = ServiceResult.Value(
                        Ride(
                            rideId = channel.id,
                            status = status ?: RideStatus.SEARCHING_FOR_DRIVER.value,
                            destinationLatitude = destLat ?: 999.0,
                            destinationLongitude = destLon ?: 999.0,
                            destinationAddress = destAddress ?: "",
                            driverId = driverId,
                            driverLatitude = driverLat,
                            driverLongitude = driverLon,
                            driverName = driverName,
                            driverAvatarUrl = driverAvatar,
                            passengerId = passengerId ?: "",
                            passengerLatitude = passengerLat ?: 999.0,
                            passengerLongitude = passengerLon ?: 999.0,
                            passengerName = passengerName ?: "",
                            passengerAvatarUrl = passengerAvatar ?: ""
                        )
                    )
                }
            }
        }

        ServiceResult.Value(Unit)
    }

    /**
     * Get a channel, if one exists, which possess uid of user, and is not in state arrived
     * 1.
     */


    override suspend fun updateRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createRide(
        passengerId: String,
        passengerName: String,
        passengerLat: Double,
        passengerLon: Double,
        passengerAvatarUrl: String,
        destinationAddress: String,
        destLat: Double,
        destLon: Double,
    ): Flow<ServiceResult<Ride?>> = flow<ServiceResult<Ride?>> {

        val channelResult = client.createChannel(
            channelType = "messaging",
            channelId = passengerId,
            memberIds = listOf(passengerId),
            extraData = mutableMapOf(
                KEY_STATUS to RideStatus.SEARCHING_FOR_DRIVER,
                KEY_PASSENGER_ID to passengerId,
                KEY_PASSENGER_NAME to passengerName,
                KEY_PASSENGER_LAT to passengerLat,
                KEY_PASSENGER_LON to passengerLon,
                KEY_PASSENGER_AVATAR_URL to passengerAvatarUrl,
                KEY_DEST_ADDRESS to destinationAddress,
                KEY_DEST_LAT to destLat,
                KEY_DEST_LON to destLon
            )
        ).await()

        channelResult.onSuccessSuspend {
            emit(
                ServiceResult.Value(
                    channelResult.data().let {
                        val extraData = it.extraData

                        Ride(
                            status = extraData[KEY_STATUS] as String? ?: "",
                            passengerId = extraData[KEY_PASSENGER_ID] as String? ?: "",
                            passengerName = extraData[KEY_PASSENGER_NAME] as String? ?: "",
                            passengerLatitude = extraData[KEY_PASSENGER_LAT] as Double? ?: 999.0,
                            passengerLongitude = extraData[KEY_PASSENGER_LON] as Double? ?: 999.0,
                            passengerAvatarUrl = extraData[KEY_PASSENGER_AVATAR_URL] as String?
                                ?: "",
                            destinationAddress = extraData[KEY_DEST_ADDRESS] as String? ?: "",
                            destinationLatitude = extraData[KEY_DEST_LAT] as Double? ?: 999.0,
                            destinationLongitude = extraData[KEY_DEST_LON] as Double? ?: 999.0,
                        )
                    }
                )
            )
        }


    }.flowOn(Dispatchers.IO)

    override suspend fun cancelRide(ride: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun completeRide(value: Ride): ServiceResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getRideByPassengerId(passengerId: String): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override suspend fun getRideByDriverId(driverId: String): ServiceResult<Ride?> {
        TODO("Not yet implemented")
    }

    override suspend fun getOpenRideRequests(driverId: String): ServiceResult<Flow<List<Ride>>> {
        TODO("Not yet implemented")
    }

}