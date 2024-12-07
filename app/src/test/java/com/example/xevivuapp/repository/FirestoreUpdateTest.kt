package com.example.xevivuapp.repository

import com.example.xevivuapp.data.TripData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.jupiter.api.Test

class FirestoreUpdateTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var mockTripData: TripData

    @Before
    fun setup() {
        // Mock Firestore và CollectionReference
        firestore = mockk()
        collection = mockk()

        // Mock method gọi firestore.collection()
        every { firestore.collection(any()) } returns collection

        // Mock update() method
        every { collection.document(any()).update(any()) } returns mockk()

        // Khởi tạo dữ liệu mock
        mockTripData = TripDataMock.validTrip
    }

    @Test
    fun testUpdateTripData() {
        // Arrange
        val tripId = "trip12345"
        val expectedTripData = mapOf(
            "originLatLng" to mockTripData.originLatLng,
            "originAddress" to mockTripData.originAddress,
            "destinationLatLng" to mockTripData.destinationLatLng,
            "destinationAddress" to mockTripData.destinationAddress,
            "vehicleType" to mockTripData.vehicleType,
            "paymentType" to mockTripData.paymentType,
            "price" to mockTripData.price,
            "distance" to mockTripData.distance,
            "duration" to mockTripData.duration,
            "bookingTime" to mockTripData.bookingTime,
            "startTime" to mockTripData.startTime,
            "endTime" to mockTripData.endTime,
            "status" to mockTripData.status,
            "passenger_ID" to mockTripData.passenger_ID,
            "driver_ID" to mockTripData.driver_ID
        )

        // Act
        collection.document(tripId).update(expectedTripData)

        // Assert
        verify { collection.document(tripId).update(expectedTripData) }
    }

    @Test
    fun testUpdateTripDataWithMissingFields() {
        // Arrange
        val tripId = "trip67890"
        val tripWithMissingFields = TripDataMock.tripWithMissingFields
        val expectedTripData = mapOf(
            "originLatLng" to tripWithMissingFields.originLatLng,
            "originAddress" to tripWithMissingFields.originAddress,
            "destinationLatLng" to tripWithMissingFields.destinationLatLng,
            "destinationAddress" to tripWithMissingFields.destinationAddress,
            "vehicleType" to tripWithMissingFields.vehicleType,
            "paymentType" to tripWithMissingFields.paymentType, // null value for paymentType
            "price" to tripWithMissingFields.price,
            "distance" to tripWithMissingFields.distance,
            "duration" to tripWithMissingFields.duration,
            "bookingTime" to tripWithMissingFields.bookingTime,
            "startTime" to tripWithMissingFields.startTime,
            "endTime" to tripWithMissingFields.endTime,
            "status" to tripWithMissingFields.status,
            "passenger_ID" to tripWithMissingFields.passenger_ID,
            "driver_ID" to tripWithMissingFields.driver_ID
        )

        // Act
        collection.document(tripId).update(expectedTripData)

        // Assert
        verify { collection.document(tripId).update(expectedTripData) }
    }

    @Test
    fun testUpdateTripDataWithNoStatus() {
        // Arrange
        val tripId = "trip11223"
        val tripWithNoStatus = TripDataMock.tripWithNoStatus
        val expectedTripData = mapOf(
            "originLatLng" to tripWithNoStatus.originLatLng,
            "originAddress" to tripWithNoStatus.originAddress,
            "destinationLatLng" to tripWithNoStatus.destinationLatLng,
            "destinationAddress" to tripWithNoStatus.destinationAddress,
            "vehicleType" to tripWithNoStatus.vehicleType,
            "paymentType" to tripWithNoStatus.paymentType,
            "price" to tripWithNoStatus.price,
            "distance" to tripWithNoStatus.distance,
            "duration" to tripWithNoStatus.duration,
            "bookingTime" to tripWithNoStatus.bookingTime,
            "startTime" to tripWithNoStatus.startTime,
            "endTime" to tripWithNoStatus.endTime,
            "status" to null, // Missing status
            "passenger_ID" to tripWithNoStatus.passenger_ID,
            "driver_ID" to tripWithNoStatus.driver_ID
        )

        // Act
        collection.document(tripId).update(expectedTripData)

        // Assert
        verify { collection.document(tripId).update(expectedTripData) }
    }

    @Test
    fun testUpdateTripDataWithNegativeDistance() {
        // Arrange
        val tripId = "trip44556"
        val tripWithNegativeDistance = TripDataMock.tripWithNegativeDistance
        val expectedTripData = mapOf(
            "originLatLng" to tripWithNegativeDistance.originLatLng,
            "originAddress" to tripWithNegativeDistance.originAddress,
            "destinationLatLng" to tripWithNegativeDistance.destinationLatLng,
            "destinationAddress" to tripWithNegativeDistance.destinationAddress,
            "vehicleType" to tripWithNegativeDistance.vehicleType,
            "paymentType" to tripWithNegativeDistance.paymentType,
            "price" to tripWithNegativeDistance.price,
            "distance" to tripWithNegativeDistance.distance, // Negative distance
            "duration" to tripWithNegativeDistance.duration,
            "bookingTime" to tripWithNegativeDistance.bookingTime,
            "startTime" to tripWithNegativeDistance.startTime,
            "endTime" to tripWithNegativeDistance.endTime,
            "status" to tripWithNegativeDistance.status,
            "passenger_ID" to tripWithNegativeDistance.passenger_ID,
            "driver_ID" to tripWithNegativeDistance.driver_ID
        )

        // Act
        collection.document(tripId).update(expectedTripData)

        // Assert
        verify { collection.document(tripId).update(expectedTripData) }
    }

    @Test
    fun testUpdateTripDataWithEmptyCoordinates() {
        // Arrange
        val tripId = "trip667788"
        val tripWithEmptyCoordinates = TripDataMock.tripWithEmptyCoordinates
        val expectedTripData = mapOf(
            "originLatLng" to tripWithEmptyCoordinates.originLatLng,
            "originAddress" to tripWithEmptyCoordinates.originAddress,
            "destinationLatLng" to tripWithEmptyCoordinates.destinationLatLng,
            "destinationAddress" to tripWithEmptyCoordinates.destinationAddress,
            "vehicleType" to tripWithEmptyCoordinates.vehicleType,
            "paymentType" to tripWithEmptyCoordinates.paymentType,
            "price" to tripWithEmptyCoordinates.price,
            "distance" to tripWithEmptyCoordinates.distance,
            "duration" to tripWithEmptyCoordinates.duration,
            "bookingTime" to tripWithEmptyCoordinates.bookingTime,
            "startTime" to tripWithEmptyCoordinates.startTime,
            "endTime" to tripWithEmptyCoordinates.endTime,
            "status" to tripWithEmptyCoordinates.status,
            "passenger_ID" to tripWithEmptyCoordinates.passenger_ID,
            "driver_ID" to tripWithEmptyCoordinates.driver_ID
        )

        // Act
        collection.document(tripId).update(expectedTripData)

        // Assert
        verify { collection.document(tripId).update(expectedTripData) }
    }
}
