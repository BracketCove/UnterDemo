package com.bracketcove.android.dashboards.passenger

sealed interface PassengerDashboardUiState {
    object RideInactive: PassengerDashboardUiState
    data class SearchingForDriver(
        val passengerLat: Double,
        val passengerLon: Double
    ): PassengerDashboardUiState
    data class PassengerPickUp(
        val passengerLat: Double,
        val passengerLon: Double,
        val driverLat: Double,
        val driverLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val driverName: String,
        val vehicleAvatar: String,
        val vehicleDescription: String
    ): PassengerDashboardUiState
    data class EnRoute(
        val passengerLat: Double,
        val passengerLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val driverName: String,
        val vehicleAvatar: String,
        val vehicleDescription: String
    ): PassengerDashboardUiState

    data class Arrived(
        val passengerLat: Double,
        val passengerLon: Double,
        val destinationLat: Double,
        val destinationLon: Double,
        val driverName: String,
        val vehicleAvatar: String,
        val vehicleDescription: String
    ): PassengerDashboardUiState

    //Signals something unexpected has happened
    object Error: PassengerDashboardUiState
    object Loading: PassengerDashboardUiState
}