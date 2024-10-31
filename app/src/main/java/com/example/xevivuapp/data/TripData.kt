package com.example.xevivuapp.data

import com.google.firebase.firestore.GeoPoint

data class TripData(
    val trip_ID :String? = null,
    val originLatLng: GeoPoint? = null,
    val originAddress: String? = null,
    val destinationLatLng: GeoPoint? = null,
    val destinationAddress: String? = null,
    val vehicleType: String? = null,
    val paymentType: String? = null,
    val price: Double? = null,
    val distance: Double? = null,
    val duration: Int? = null,
    val bookingTime: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: String? = null,
    val passenger_ID: String? = null,
    val driver_ID: String? = null
)