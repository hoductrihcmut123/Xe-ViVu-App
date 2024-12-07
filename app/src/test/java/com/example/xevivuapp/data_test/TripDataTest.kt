package com.example.xevivuapp.data_test

import com.example.xevivuapp.data.TripData
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TripDataTest {

    @Test
    fun `test TripData default initialization`() {
        // Arrange & Act
        val tripData = TripData()

        // Assert
        assertNull(tripData.trip_ID)
        assertNull(tripData.originLatLng)
        assertNull(tripData.originAddress)
        assertNull(tripData.destinationLatLng)
        assertNull(tripData.destinationAddress)
        assertNull(tripData.vehicleType)
        assertNull(tripData.paymentType)
        assertNull(tripData.price)
        assertNull(tripData.distance)
        assertNull(tripData.duration)
        assertNull(tripData.bookingTime)
        assertNull(tripData.startTime)
        assertNull(tripData.endTime)
        assertNull(tripData.status)
        assertNull(tripData.passenger_ID)
        assertNull(tripData.driver_ID)
    }

    @Test
    fun `test TripData full initialization`() {
        // Arrange
        val originLatLng = GeoPoint(10.8231, 106.6297)
        val destinationLatLng = GeoPoint(10.7769, 106.7009)

        val tripData = TripData(
            trip_ID = "T001",
            originLatLng = originLatLng,
            originAddress = "Ho Chi Minh City",
            destinationLatLng = destinationLatLng,
            destinationAddress = "District 1, HCMC",
            vehicleType = "Car",
            paymentType = "Credit Card",
            price = 150000.0,
            distance = 10.5,
            duration = 20,
            bookingTime = "2024-12-07T10:00:00Z",
            startTime = "2024-12-07T10:05:00Z",
            endTime = "2024-12-07T10:25:00Z",
            status = "Completed",
            passenger_ID = "P001",
            driver_ID = "D001"
        )

        // Assert
        assertEquals("T001", tripData.trip_ID)
        assertEquals(originLatLng, tripData.originLatLng)
        assertEquals("Ho Chi Minh City", tripData.originAddress)
        assertEquals(destinationLatLng, tripData.destinationLatLng)
        assertEquals("District 1, HCMC", tripData.destinationAddress)
        assertEquals("Car", tripData.vehicleType)
        assertEquals("Credit Card", tripData.paymentType)
        tripData.price?.let { assertEquals(150000.0, it, 0.0) }
        tripData.distance?.let { assertEquals(10.5, it, 0.0) }
        assertEquals(20, tripData.duration)
        assertEquals("2024-12-07T10:00:00Z", tripData.bookingTime)
        assertEquals("2024-12-07T10:05:00Z", tripData.startTime)
        assertEquals("2024-12-07T10:25:00Z", tripData.endTime)
        assertEquals("Completed", tripData.status)
        assertEquals("P001", tripData.passenger_ID)
        assertEquals("D001", tripData.driver_ID)
    }

    @Test
    fun `test TripData equality for identical objects`() {
        // Arrange
        val originLatLng = GeoPoint(10.8231, 106.6297)
        val destinationLatLng = GeoPoint(10.7769, 106.7009)

        val tripData1 = TripData(
            trip_ID = "T001",
            originLatLng = originLatLng,
            originAddress = "Ho Chi Minh City",
            destinationLatLng = destinationLatLng,
            destinationAddress = "District 1, HCMC",
            vehicleType = "Car",
            paymentType = "Credit Card",
            price = 150000.0,
            distance = 10.5,
            duration = 20,
            bookingTime = "2024-12-07T10:00:00Z",
            startTime = "2024-12-07T10:05:00Z",
            endTime = "2024-12-07T10:25:00Z",
            status = "Completed",
            passenger_ID = "P001",
            driver_ID = "D001"
        )

        val tripData2 = tripData1.copy()

        // Assert
        assertEquals(tripData1, tripData2)
        assertEquals(tripData1.hashCode(), tripData2.hashCode())
    }

    @Test
    fun `test TripData inequality for different objects`() {
        // Arrange
        val tripData1 = TripData(trip_ID = "T001")
        val tripData2 = TripData(trip_ID = "T002")

        // Assert
        assertNotEquals(tripData1, tripData2)
    }

    @Test
    fun `test TripData copy functionality`() {
        // Arrange
        val tripData = TripData(
            trip_ID = "T001",
            originAddress = "Ho Chi Minh City"
        )

        // Act
        val updatedTripData = tripData.copy(originAddress = "Changed Address")

        // Assert
        assertEquals("Changed Address", updatedTripData.originAddress)
        assertEquals("T001", updatedTripData.trip_ID)
    }

    @Test
    fun `test TripData nullability of optional fields`() {
        // Arrange
        val tripData = TripData(
            trip_ID = "T001",
            price = null,
            distance = null
        )

        // Assert
        assertNull(tripData.price)
        assertNull(tripData.distance)
    }

    @Test
    fun `test TripData handles large values`() {
        // Arrange
        val tripData = TripData(
            price = Double.MAX_VALUE,
            distance = Double.MAX_VALUE,
            duration = Int.MAX_VALUE
        )

        // Assert
        tripData.price?.let { assertEquals(Double.MAX_VALUE, it, 0.0) }
        tripData.distance?.let { assertEquals(Double.MAX_VALUE, it, 0.0) }
        assertEquals(Int.MAX_VALUE, tripData.duration)
    }

    @Test
    fun `test TripData handles negative values`() {
        // Arrange
        val tripData = TripData(
            price = -5000.0,
            distance = -10.0,
            duration = -30
        )

        // Assert
        tripData.price?.let { assertEquals(-5000.0, it, 0.0) }
        tripData.distance?.let { assertEquals(-10.0, it, 0.0) }
        assertEquals(-30, tripData.duration)
    }

    @Test
    fun `test TripData with empty strings`() {
        // Arrange
        val tripData = TripData(
            trip_ID = "",
            originAddress = "",
            destinationAddress = "",
            vehicleType = "",
            paymentType = "",
            status = "",
            passenger_ID = "",
            driver_ID = ""
        )

        // Assert
        assertEquals("", tripData.trip_ID)
        assertEquals("", tripData.originAddress)
        assertEquals("", tripData.destinationAddress)
        assertEquals("", tripData.vehicleType)
        assertEquals("", tripData.paymentType)
        assertEquals("", tripData.status)
        assertEquals("", tripData.passenger_ID)
        assertEquals("", tripData.driver_ID)
    }

    @Test
    fun `test TripData GeoPoint values`() {
        // Arrange
        val originLatLng = GeoPoint(0.0, 0.0)
        val destinationLatLng = GeoPoint(90.0, 180.0)

        val tripData = TripData(
            originLatLng = originLatLng,
            destinationLatLng = destinationLatLng
        )

        // Assert
        tripData.originLatLng?.latitude?.let { assertEquals(0.0, it, 0.0) }
        tripData.originLatLng?.longitude?.let { assertEquals(0.0, it, 0.0) }
        tripData.destinationLatLng?.latitude?.let { assertEquals(90.0, it, 0.0) }
        tripData.destinationLatLng?.longitude?.let { assertEquals(180.0, it, 0.0) }
    }

    @Test
    fun `test TripData with partial data`() {
        // Arrange
        val tripData = TripData(
            trip_ID = "T123",
            originAddress = "123 Main St",
            status = "Active"
        )

        // Assert
        assertEquals("T123", tripData.trip_ID)
        assertEquals("123 Main St", tripData.originAddress)
        assertEquals("Active", tripData.status)
        assertNull(tripData.destinationAddress)
        assertNull(tripData.price)
    }

    @Test
    fun `test TripData with all null values`() {
        // Arrange
        val tripData = TripData()

        // Assert
        tripData.apply {
            assertNull(trip_ID)
            assertNull(originLatLng)
            assertNull(originAddress)
            assertNull(destinationLatLng)
            assertNull(destinationAddress)
            assertNull(vehicleType)
            assertNull(paymentType)
            assertNull(price)
            assertNull(distance)
            assertNull(duration)
            assertNull(bookingTime)
            assertNull(startTime)
            assertNull(endTime)
            assertNull(status)
            assertNull(passenger_ID)
            assertNull(driver_ID)
        }
    }

    @Test
    fun `test TripData status transitions`() {
        // Arrange
        val tripData = TripData(status = "Pending")

        // Act
        val updatedTripData = tripData.copy(status = "Completed")

        // Assert
        assertEquals("Pending", tripData.status)
        assertEquals("Completed", updatedTripData.status)
    }

    @Test
    fun `test TripData object to string conversion`() {
        // Arrange
        val tripData = TripData(
            trip_ID = "T001",
            originAddress = "Origin",
            destinationAddress = "Destination",
            status = "Completed"
        )

        // Act
        val tripDataString = tripData.toString()

        // Assert
        assertTrue(tripDataString.contains("T001"))
        assertTrue(tripDataString.contains("Origin"))
        assertTrue(tripDataString.contains("Destination"))
        assertTrue(tripDataString.contains("Completed"))
    }

    @Test
    fun `test TripData equality with different nullable values`() {
        // Arrange
        val tripData1 = TripData(price = null)
        val tripData2 = TripData(price = 100.0)

        // Assert
        assertNotEquals(tripData1, tripData2)
    }

    @Test
    fun `test TripData hashCode with same values`() {
        // Arrange
        val tripData1 = TripData(trip_ID = "T001")
        val tripData2 = TripData(trip_ID = "T001")

        // Assert
        assertEquals(tripData1.hashCode(), tripData2.hashCode())
    }
}
