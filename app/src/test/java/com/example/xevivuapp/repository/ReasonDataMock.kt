package com.example.xevivuapp.repository

import com.example.xevivuapp.data.ReasonData
import com.google.firebase.firestore.DocumentReference
import io.mockk.mockk

object ReasonDataMock {

    val validReasonData = ReasonData(
        reason_ID = "reason12345",
        passengerCancelReason = "Passenger changed their mind.",
        driverCancelReason = "Driver reached destination too late.",
        driverCancelEmergency = "No emergency.",
        driverCancelEmergencyDetail = "N/A",
        feedbackPassengerRef = null,  // Mock value can be set if required
        feedbackDriverRef = null  // Mock value can be set if required
    )

    val reasonWithPassengerCancel = ReasonData(
        reason_ID = "reason67890",
        passengerCancelReason = "Passenger had an emergency.",
        driverCancelReason = "N/A",
        driverCancelEmergency = "Passenger had a medical emergency.",
        driverCancelEmergencyDetail = "Passenger needed immediate medical attention.",
        feedbackPassengerRef = null,
        feedbackDriverRef = null
    )

    val reasonWithDriverCancel = ReasonData(
        reason_ID = "reason11223",
        passengerCancelReason = "N/A",
        driverCancelReason = "Driver had a flat tire.",
        driverCancelEmergency = "Driver had car trouble.",
        driverCancelEmergencyDetail = "Flat tire on the way to the passenger.",
        feedbackPassengerRef = null,
        feedbackDriverRef = null
    )

    val reasonWithEmptyFields = ReasonData(
        reason_ID = "",
        passengerCancelReason = "",
        driverCancelReason = "",
        driverCancelEmergency = "",
        driverCancelEmergencyDetail = "",
        feedbackPassengerRef = null,
        feedbackDriverRef = null
    )

    val reasonWithFeedbackRefs = ReasonData(
        reason_ID = "reason33445",
        passengerCancelReason = "Passenger had a scheduling conflict.",
        driverCancelReason = "Driver was late.",
        driverCancelEmergency = "Driver had a personal emergency.",
        driverCancelEmergencyDetail = "Driver needed to deal with a family emergency.",
        feedbackPassengerRef = mockk<DocumentReference>(),  // Use mockk to simulate the DocumentReference
        feedbackDriverRef = mockk<DocumentReference>()  // Use mockk to simulate the DocumentReference
    )

    val reasonWithMissingDetails = ReasonData(
        reason_ID = "reason55667",
        passengerCancelReason = "Passenger no longer needed the ride.",
        driverCancelReason = "",
        driverCancelEmergency = "Driver had a car breakdown.",
        driverCancelEmergencyDetail = "",  // Missing detailed information
        feedbackPassengerRef = null,
        feedbackDriverRef = null
    )
}
