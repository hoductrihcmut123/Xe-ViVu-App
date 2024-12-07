package com.example.xevivuapp.repository

import com.example.xevivuapp.data.TripData
import com.google.firebase.firestore.GeoPoint

object TripDataMock {

    val validTrip = TripData(
        trip_ID = "trip12345",
        originLatLng = GeoPoint(10.8231, 106.6297), // Example coordinates (Ho Chi Minh City)
        originAddress = "123, ABC Street, Ho Chi Minh City",
        destinationLatLng = GeoPoint(10.762622, 106.660172), // Example coordinates (District 3, HCMC)
        destinationAddress = "456, XYZ Avenue, District 3, Ho Chi Minh City",
        vehicleType = "Sedan",
        paymentType = "Cash",
        price = 200000.0,
        distance = 10.5,
        duration = 25,
        bookingTime = "2024-12-01T14:30:00",
        startTime = "2024-12-01T14:45:00",
        endTime = "2024-12-01T15:10:00",
        status = "Completed",
        passenger_ID = "passenger123",
        driver_ID = "driver123"
    )

    val tripWithMissingFields = TripData(
        trip_ID = "trip67890",
        originLatLng = GeoPoint(21.0285, 105.8542), // Example coordinates (Hanoi)
        originAddress = "789, DEF Street, Hanoi",
        destinationLatLng = GeoPoint(21.0285, 105.8542),
        destinationAddress = "321, GHI Road, Hanoi",
        vehicleType = "SUV",
        paymentType = null, // Missing paymentType
        price = 300000.0,
        distance = 15.3,
        duration = 40,
        bookingTime = "2024-12-01T16:00:00",
        startTime = "2024-12-01T16:15:00",
        endTime = "2024-12-01T16:55:00",
        status = "Ongoing",
        passenger_ID = "passenger456",
        driver_ID = "driver456"
    )

    val tripWithNoStatus = TripData(
        trip_ID = "trip11223",
        originLatLng = GeoPoint(14.0583, 108.2113), // Example coordinates (Da Nang)
        originAddress = "123, JKL Street, Da Nang",
        destinationLatLng = GeoPoint(14.0583, 108.2113),
        destinationAddress = "456, MNO Road, Da Nang",
        vehicleType = "Motorbike",
        paymentType = "Card",
        price = 50000.0,
        distance = 5.2,
        duration = 15,
        bookingTime = "2024-12-02T12:00:00",
        startTime = "2024-12-02T12:10:00",
        endTime = "2024-12-02T12:25:00",
        status = null, // Missing status
        passenger_ID = "passenger789",
        driver_ID = "driver789"
    )

    val tripWithNegativeDistance = TripData(
        trip_ID = "trip44556",
        originLatLng = GeoPoint(19.0760, 72.8777), // Example coordinates (Mumbai)
        originAddress = "10, PQR Street, Mumbai",
        destinationLatLng = GeoPoint(19.0760, 72.8777),
        destinationAddress = "50, STU Road, Mumbai",
        vehicleType = "Hatchback",
        paymentType = "Cash",
        price = 100000.0,
        distance = -2.5, // Invalid negative distance
        duration = 20,
        bookingTime = "2024-12-03T18:00:00",
        startTime = "2024-12-03T18:10:00",
        endTime = "2024-12-03T18:30:00",
        status = "Cancelled",
        passenger_ID = "passenger101",
        driver_ID = "driver101"
    )

    val tripWithEmptyCoordinates = TripData(
        trip_ID = "trip667788",
        originLatLng = GeoPoint(0.0, 0.0), // Invalid coordinates
        originAddress = "N/A",
        destinationLatLng = GeoPoint(0.0, 0.0), // Invalid coordinates
        destinationAddress = "N/A",
        vehicleType = "Van",
        paymentType = "Online",
        price = 75000.0,
        distance = 7.8,
        duration = 30,
        bookingTime = "2024-12-04T10:00:00",
        startTime = "2024-12-04T10:15:00",
        endTime = "2024-12-04T10:45:00",
        status = "Pending",
        passenger_ID = "passenger102",
        driver_ID = "driver102"
    )
}
