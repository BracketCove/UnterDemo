package com.bracketcove.authorization

import android.util.Log
import com.bracketcove.ServiceResult
import com.bracketcove.constants.*
import com.bracketcove.domain.Ride
import com.bracketcove.domain.RideStatus
import com.bracketcove.domain.UnterUser
import com.bracketcove.rides.RideService
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.events.ChannelDeletedEvent
import io.getstream.chat.android.client.events.ChannelUpdatedByUserEvent
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.utils.observable.Disposable
import io.getstream.chat.android.client.utils.onErrorSuspend
import io.getstream.chat.android.client.utils.onSuccessSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class StreamRideService(
    private val client: ChatClient
) : RideService {

    private val _rideModelUpdates: MutableStateFlow<ServiceResult<Ride?>> =
        MutableStateFlow(ServiceResult.Value(null))

    private val _openRides: MutableStateFlow<ServiceResult<List<Ride>>> =
        MutableStateFlow(ServiceResult.Value(emptyList()))

    override fun openRides(): Flow<ServiceResult<List<Ride>>> = _openRides
    override fun rideFlow(): Flow<ServiceResult<Ride?>> = _rideModelUpdates

    private val disposables: MutableList<Disposable> = mutableListOf()

    override suspend fun observeRideById(rideId: String) {
        withContext(Dispatchers.IO) {
            val channelClient = client.channel(
                cid = rideId
            )

            val result = channelClient.addMembers(listOf(client.getCurrentUser()?.id ?: "")).await()

            if (result.isSuccess) {
                val watchResult = channelClient.watch().await()

                observeChannelEvents(channelClient)

                watchResult.onSuccessSuspend { channel ->
                    _rideModelUpdates.emit(
                        channel.let {
                            //TODO figure out if this if else actually does something useful
                            if (channel.hidden != null && channel.hidden!!) {
                                ServiceResult.Value(null)
                            } else {
                                ServiceResult.Value(
                                    streamChannelToRide(channel)
                                )
                            }
                        }
                    )
                }

                watchResult.onErrorSuspend {
                    _rideModelUpdates.emit(
                        result.error().let {
                            ServiceResult.Failure(Exception(it.cause))
                        }
                    )
                }

            } else {
                _rideModelUpdates.emit(
                    result.error().let {
                        ServiceResult.Failure(Exception(it.cause))
                    }
                )
            }
        }
    }

    private suspend fun observeChannelEvents(channelClient: ChannelClient) {
        channelClient.subscribe { event: ChatEvent ->
            when (event) {
                is ChannelDeletedEvent -> {
                    _rideModelUpdates.value = ServiceResult.Value(null)
                }

                is ChannelUpdatedByUserEvent -> { _rideModelUpdates.value =
                    ServiceResult.Value(streamChannelToRide(event.channel))
                }

                else -> {
                    Log.d("EVENT", event.type)
                }
            }
        }

//        channelClient.subscribeFor<ChannelUpdatedEvent> { event ->
//            val channel = event.channel
//            _rideModelUpdates.value = ServiceResult.Value(streamChannelToRide(channel))
//        }.also { disposables.add(it) }
//
//        channelClient.subscribeFor<ChannelDeletedEvent> {
//            _rideModelUpdates.value = ServiceResult.Value(null)
//          //  disposables.forEach { it.dispose() }
//        }
    }


    private fun streamChannelToRide(channel: Channel): Ride {
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
        val passengerAvatar: String? =
            extraData[KEY_PASSENGER_AVATAR_URL] as String?
        val passengerName: String? = extraData[KEY_PASSENGER_NAME] as String?
        val status: String? = extraData[KEY_STATUS] as String?

        return Ride(
            rideId = channel.cid,
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
    }

    override suspend fun observeOpenRides() {
        withContext(Dispatchers.IO) {
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.eq(KEY_STATUS, RideStatus.SEARCHING_FOR_DRIVER.value)
                ),
                querySort = QuerySortByField.descByName(FILTER_CREATED_AT),
                limit = 10
            )

            val result = client.queryChannels(request).await()

            if (result.isSuccess) {
                _openRides.emit(ServiceResult.Value(
                    result.data().map { channel ->
                        streamChannelToRide(channel)
                    }
                )
                )
            } else {
                _openRides.emit(ServiceResult.Failure(Exception(result.error().cause)))
            }
        }
    }

    override suspend fun connectDriverToRide(
        ride: Ride,
        driver: UnterUser
    ): ServiceResult<String> =
        withContext(Dispatchers.IO) {
            val channelClient = client.channel(
                cid = ride.rideId
            )

            val addToChannel =
                channelClient.addMembers(listOf(client.getCurrentUser()?.id ?: "")).await()
            if (addToChannel.isSuccess) {

                //note: we must check in the VM if driverLatLng are null!!
                val updateDetails = channelClient.updatePartial(
                    set = mutableMapOf(
                        KEY_STATUS to RideStatus.PASSENGER_PICK_UP.value,
                        KEY_DRIVER_ID to driver.userId,
                        KEY_DRIVER_NAME to driver.username,
                        KEY_DRIVER_LAT to ride.driverLatitude!!,
                        KEY_DRIVER_LON to ride.driverLongitude!!,
                        KEY_DRIVER_AVATAR_URL to driver.avatarPhotoUrl
                    )
                ).await()

                if (updateDetails.isSuccess) {
                    ServiceResult.Value(channelClient.cid)
                } else {
                    ServiceResult.Failure(Exception(updateDetails.error().cause))
                }
            } else {
                ServiceResult.Failure(Exception(addToChannel.error().cause))
            }
        }

    override suspend fun getRideIfInProgress(): ServiceResult<String?> =
        withContext(Dispatchers.IO) {

            val currentUserId = client.getCurrentUser()?.id ?: ""
            val request = QueryChannelsRequest(
                filter = Filters.and(
                    Filters.`in`("members", currentUserId)
                ),
                querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
                limit = 1
            )

            val result = client.queryChannels(request).await()

            if (result.isSuccess) {
                if (result.data().isEmpty()) ServiceResult.Value(null)
                else {
                    result.data().first().let { channel ->
                        ServiceResult.Value(channel.cid)
                    }
                }
            } else {
                ServiceResult.Failure(Exception(result.error().cause))
            }
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
            channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
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
            ServiceResult.Value(result.data().cid)
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

    override suspend fun cancelRide(): ServiceResult<Unit> = withContext(Dispatchers.IO) {
        //get ride in progress
        val currentUserId = client.getCurrentUser()?.id ?: ""
        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.`in`("members", currentUserId)
            ),
            querySort = QuerySortByField.descByName(FILTER_UPDATED_AT),
            limit = 1
        )

        val result = client.queryChannels(request).await()

        if (result.isSuccess) {
            if (result.data()
                    .isEmpty()
            ) ServiceResult.Failure(Exception("Failed to retrieve channel for cancellation"))
            else {
                val channelClient = client.channel(result.data().first().cid)


                if (channelClient.hide().await().isSuccess) {
                    val deleteResult = channelClient.delete().await()

                    if (deleteResult.isSuccess) {
                        _rideModelUpdates.emit(ServiceResult.Value(null))
                        ServiceResult.Value(Unit)
                    } else ServiceResult.Failure(Exception(result.error().cause))
                } else {
                    ServiceResult.Failure(Exception("Unable to hide channel"))
                }


            }
        } else {
            ServiceResult.Failure(Exception(result.error().cause))
        }
    }

    override suspend fun completeRide(ride: Ride): ServiceResult<Unit> {
        val channelClient = client.channel(ride.rideId)
        channelClient.delete().await()
        return ServiceResult.Value(Unit)
    }

    override suspend fun advanceRide(rideId: String, newState: String): ServiceResult<Unit> =
        withContext(Dispatchers.IO) {
            val advanceRide = client.updateChannelPartial(
                channelType = STREAM_CHANNEL_TYPE_LIVESTREAM,
                channelId = getChannelIdOnly(rideId),
                set = mutableMapOf(
                    KEY_STATUS to newState
                )
            ).await()

            if (advanceRide.isSuccess) {
                ServiceResult.Value(Unit)
            } else {
                ServiceResult.Failure(
                    Exception(advanceRide.error().cause)
                )
            }
        }

    //A cid will be passed in here in the format livestream:<channel id>. This splits it into
    //just the channel id.
    private fun getChannelIdOnly(cid: String): String = cid.split(":").last()

}