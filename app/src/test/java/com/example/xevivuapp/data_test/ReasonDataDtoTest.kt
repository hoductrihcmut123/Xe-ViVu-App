package com.example.xevivuapp.data_test

import com.example.xevivuapp.data.ReasonDataDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReasonDataDtoTest {

    @Test
    fun `test ReasonDataDto default initialization`() {
        // Arrange & Act
        val reasonData = ReasonDataDto()

        // Assert
        assertEquals("", reasonData.reason_ID)
        assertEquals("", reasonData.passengerCancelReason)
        assertEquals("", reasonData.driverCancelReason)
        assertEquals("", reasonData.driverCancelEmergency)
        assertEquals("", reasonData.driverCancelEmergencyDetail)
        assertNull(reasonData.feedbackPassengerRef)
        assertNull(reasonData.feedbackDriverRef)
    }

    @Test
    fun `test ReasonDataDto full initialization`() {
        // Arrange
        val reasonData = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable",
            driverCancelEmergency = "Car breakdown",
            driverCancelEmergencyDetail = "Flat tire",
            feedbackPassengerRef = "feedback_passenger_001",
            feedbackDriverRef = "feedback_driver_001"
        )

        // Assert
        assertEquals("R001", reasonData.reason_ID)
        assertEquals("Passenger was late", reasonData.passengerCancelReason)
        assertEquals("Driver unavailable", reasonData.driverCancelReason)
        assertEquals("Car breakdown", reasonData.driverCancelEmergency)
        assertEquals("Flat tire", reasonData.driverCancelEmergencyDetail)
        assertEquals("feedback_passenger_001", reasonData.feedbackPassengerRef)
        assertEquals("feedback_driver_001", reasonData.feedbackDriverRef)
    }

    @Test
    fun `test ReasonDataDto equality for identical objects`() {
        // Arrange
        val reasonData1 = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable",
            driverCancelEmergency = "Car breakdown",
            driverCancelEmergencyDetail = "Flat tire",
            feedbackPassengerRef = "feedback_passenger_001",
            feedbackDriverRef = "feedback_driver_001"
        )

        val reasonData2 = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable",
            driverCancelEmergency = "Car breakdown",
            driverCancelEmergencyDetail = "Flat tire",
            feedbackPassengerRef = "feedback_passenger_001",
            feedbackDriverRef = "feedback_driver_001"
        )

        // Assert
        assertEquals(reasonData1, reasonData2)
        assertEquals(reasonData1.hashCode(), reasonData2.hashCode())
    }

    @Test
    fun `test ReasonDataDto inequality for different objects`() {
        // Arrange
        val reasonData1 = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable"
        )

        val reasonData2 = ReasonDataDto(
            reason_ID = "R002",
            passengerCancelReason = "Wrong destination",
            driverCancelReason = "Other commitments"
        )

        // Assert
        assertNotEquals(reasonData1, reasonData2)
    }

    @Test
    fun `test ReasonDataDto toString readability`() {
        // Arrange
        val reasonData = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable"
        )

        // Act
        val toStringOutput = reasonData.toString()

        // Assert
        assertTrue(toStringOutput.contains("R001"))
        assertTrue(toStringOutput.contains("Passenger was late"))
        assertTrue(toStringOutput.contains("Driver unavailable"))
    }

    @Test
    fun `test ReasonDataDto copy functionality`() {
        // Arrange
        val reasonData = ReasonDataDto(
            reason_ID = "R001",
            passengerCancelReason = "Passenger was late",
            driverCancelReason = "Driver unavailable"
        )

        // Act
        val updatedReasonData = reasonData.copy(passengerCancelReason = "Changed reason")

        // Assert
        assertEquals("Changed reason", updatedReasonData.passengerCancelReason)
        assertEquals(reasonData.reason_ID, updatedReasonData.reason_ID)
        assertEquals(reasonData.driverCancelReason, updatedReasonData.driverCancelReason)
    }

    @Test
    fun `test ReasonDataDto nullability of optional fields`() {
        // Arrange
        val reasonData = ReasonDataDto(
            reason_ID = "R001",
            feedbackPassengerRef = null,
            feedbackDriverRef = null
        )

        // Assert
        assertNull(reasonData.feedbackPassengerRef)
        assertNull(reasonData.feedbackDriverRef)
    }
}
