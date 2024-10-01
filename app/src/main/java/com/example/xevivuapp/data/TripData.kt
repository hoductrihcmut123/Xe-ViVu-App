package com.example.xevivuapp.data

import com.google.android.gms.maps.model.LatLng

data class TripData(
    val trip_ID :String? = null,
    val originLatLng: LatLng? = null,
    val originAddress: String? = null,
    val destinationLatLng: LatLng? = null,
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
    val passsenger_ID: String? = null,
    val driver_ID: String? = null
)