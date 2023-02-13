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
import io.getstream.chat.android.offline.extensions.watchChannelAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class StreamRideService(
    private val client: ChatClient
) : RideService {

    private val _rideModelUpdates: MutableStateFlow<ServiceResult<Ride?>> =
        MutableStateFlow(ServiceResult.Value(null))
    private val rideModelUpdates: Flow<ServiceResult<Ride?>> = _rideModelUpdates
    override fun rideFlow(): Flow<ServiceResult<Ride?>> = rideModelUpdates

    override suspend fun observeRideById(rideId: String) {
        val channelClient = client.channel(
            channelType = "messaging",
            channelId = rideId
        )

        val result = channelClient.addMembers(listOf(client.getCurrentUser()?.id ?: "")).await()

        if (result.isSuccess) {
            _rideModelUpdates.emit(
                result.data().let { channel ->
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

                    ServiceResult.Value(
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
            )
        } else {
            _rideModelUpdates.emit(
                result.error().let {
                    ServiceResult.Failure(Exception(it.cause))
                }
            )
        }
    }


    override suspend fun getRideIfInProgress(): ServiceResult<String?> =
        withContext(Dispatchers.IO) {
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq(STREAM_USER_ID, client.getCurrentUser()?.id ?: "")
                ),
                querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
                limit = 1
            )

            val result = client.queryChannels(request).await()

            if (result.isSuccess) {
                if (result.data().isEmpty()) ServiceResult.Value(null)
                else {
                    result.data().first().let { channel ->
                        ServiceResult.Value(channel.id)
                    }
                }
            } else {
                ServiceResult.Failure(Exception(result.error().cause))
            }
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
        destLon: Double
    ): ServiceResult<String> = withContext(Dispatchers.IO) {

        val channelId = generateUniqueId(6, ('A'..'Z') + ('0'..'9'))
        val result = client.createChannel(
            channelType = "messaging",
            channelId = channelId,
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

        if (result.isSuccess) {
                ServiceResult.Value(channelId)
        } else {
            ServiceResult.Failure(Exception(result.error().cause))
        }
    }

    //Function below taken from https://github.com/GetStream/stream-draw-android
    private fun generateUniqueId(length: Int, allowedChars: List<Char>): String {
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

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