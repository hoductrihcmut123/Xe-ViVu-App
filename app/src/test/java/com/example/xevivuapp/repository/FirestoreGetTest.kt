package com.example.xevivuapp.repository

import com.example.xevivuapp.data.ReasonData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FirestoreGetTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var mockReasonData: ReasonData

    @Before
    fun setup() {
        // Mock Firestore và CollectionReference
        firestore = mockk()
        collection = mockk()

        // Mock method gọi firestore.collection()
        every { firestore.collection(any()) } returns collection

        // Mock get() method trả về tài liệu mock
        every { collection.document(any()).get() } returns mockk {
            every { result } returns mockk<DocumentSnapshot> {
                // Mock các trường dữ liệu bên trong DocumentSnapshot
//                every { get("reason_ID") } returns "reason12345"
//                every { get("passengerCancelReason") } returns "Passenger changed their mind."
//                every { get("driverCancelReason") } returns "Driver reached destination too late."
//                every { get("driverCancelEmergency") } returns "No emergency."
//                every { get("driverCancelEmergencyDetail") } returns "N/A"
//                every { get("feedbackPassengerRef") } returns null
//                every { get("feedbackDriverRef") } returns null
            }
        }

        // Khởi tạo dữ liệu mock
        mockReasonData = ReasonDataMock.validReasonData
    }

    @Test
    fun testGetReasonData() {
        // Arrange
        val reasonId = "reason12345"

        // Act
        val documentSnapshot = collection.document(reasonId).get().result
        val reasonData = ReasonData(
            reason_ID = documentSnapshot?.get("reason_ID") as String,
            passengerCancelReason = documentSnapshot.get("passengerCancelReason") as String,
            driverCancelReason = documentSnapshot.get("driverCancelReason") as String,
            driverCancelEmergency = documentSnapshot.get("driverCancelEmergency") as String,
            driverCancelEmergencyDetail = documentSnapshot.get("driverCancelEmergencyDetail") as String,
            feedbackPassengerRef = documentSnapshot.get("feedbackPassengerRef") as DocumentReference?,
            feedbackDriverRef = documentSnapshot.get("feedbackDriverRef") as DocumentReference?
        )

        // Assert
        assertEquals(reasonData.reason_ID, mockReasonData.reason_ID)
        assertEquals(reasonData.passengerCancelReason, mockReasonData.passengerCancelReason)
        assertEquals(reasonData.driverCancelReason, mockReasonData.driverCancelReason)
        assertEquals(reasonData.driverCancelEmergency, mockReasonData.driverCancelEmergency)
        assertEquals(reasonData.driverCancelEmergencyDetail, mockReasonData.driverCancelEmergencyDetail)
        assertEquals(reasonData.feedbackPassengerRef, mockReasonData.feedbackPassengerRef)
        assertEquals(reasonData.feedbackDriverRef, mockReasonData.feedbackDriverRef)
    }

    @Test
    fun testGetReasonDataWithMissingFields() {
        // Arrange
        val reasonId = "reason55667"
        val missingFieldsReason = ReasonDataMock.reasonWithMissingDetails

        // Mock dữ liệu trả về cho tài liệu với thiếu trường
        every { collection.document(reasonId).get() } returns mockk {
            every { result } returns mockk<DocumentSnapshot> {
//                every { get("reason_ID") } returns "reason55667"
//                every { get("passengerCancelReason") } returns "Passenger no longer needed the ride."
//                every { get("driverCancelReason") } returns ""
//                every { get("driverCancelEmergency") } returns "Driver had a car breakdown."
//                every { get("driverCancelEmergencyDetail") } returns ""
//                every { get("feedbackPassengerRef") } returns null
//                every { get("feedbackDriverRef") } returns null
            }
        }

        // Act
        val documentSnapshot = collection.document(reasonId).get().result
        val reasonData = ReasonData(
            reason_ID = documentSnapshot?.get("reason_ID") as String,
            passengerCancelReason = documentSnapshot.get("passengerCancelReason") as String,
            driverCancelReason = documentSnapshot.get("driverCancelReason") as String,
            driverCancelEmergency = documentSnapshot.get("driverCancelEmergency") as String,
            driverCancelEmergencyDetail = documentSnapshot.get("driverCancelEmergencyDetail") as String,
            feedbackPassengerRef = documentSnapshot.get("feedbackPassengerRef") as DocumentReference?,
            feedbackDriverRef = documentSnapshot.get("feedbackDriverRef") as DocumentReference?
        )

        // Assert
        assertEquals(reasonData.reason_ID, missingFieldsReason.reason_ID)
        assertEquals(reasonData.passengerCancelReason, missingFieldsReason.passengerCancelReason)
        assertEquals(reasonData.driverCancelReason, missingFieldsReason.driverCancelReason)
        assertEquals(reasonData.driverCancelEmergency, missingFieldsReason.driverCancelEmergency)
        assertEquals(reasonData.driverCancelEmergencyDetail, missingFieldsReason.driverCancelEmergencyDetail)
        assertEquals(reasonData.feedbackPassengerRef, missingFieldsReason.feedbackPassengerRef)
        assertEquals(reasonData.feedbackDriverRef, missingFieldsReason.feedbackDriverRef)
    }

    @Test
    fun testGetReasonDataWithFeedbackRefs() {
        // Arrange
        val reasonId = "reason33445"
        val reasonWithFeedback = ReasonDataMock.reasonWithFeedbackRefs

        // Mock dữ liệu trả về cho tài liệu có feedbackRefs
        every { collection.document(reasonId).get() } returns mockk {
            every { result } returns mockk<DocumentSnapshot> {
//                every { get("reason_ID") } returns "reason33445"
//                every { get("passengerCancelReason") } returns "Passenger had a scheduling conflict."
//                every { get("driverCancelReason") } returns "Driver was late."
//                every { get("driverCancelEmergency") } returns "Driver had a personal emergency."
//                every { get("driverCancelEmergencyDetail") } returns "Driver needed to deal with a family emergency."
//                every { get("feedbackPassengerRef") } returns mockk<DocumentReference>()
//                every { get("feedbackDriverRef") } returns mockk<DocumentReference>()
            }
        }

        // Act
        val documentSnapshot = collection.document(reasonId).get().result
        val reasonData = ReasonData(
            reason_ID = documentSnapshot?.get("reason_ID") as String,
            passengerCancelReason = documentSnapshot.get("passengerCancelReason") as String,
            driverCancelReason = documentSnapshot.get("driverCancelReason") as String,
            driverCancelEmergency = documentSnapshot.get("driverCancelEmergency") as String,
            driverCancelEmergencyDetail = documentSnapshot.get("driverCancelEmergencyDetail") as String,
            feedbackPassengerRef = documentSnapshot.get("feedbackPassengerRef") as DocumentReference?,
            feedbackDriverRef = documentSnapshot.get("feedbackDriverRef") as DocumentReference?
        )

        // Assert
        assertEquals(reasonData.reason_ID, reasonWithFeedback.reason_ID)
        assertEquals(reasonData.passengerCancelReason, reasonWithFeedback.passengerCancelReason)
        assertEquals(reasonData.driverCancelReason, reasonWithFeedback.driverCancelReason)
        assertEquals(reasonData.driverCancelEmergency, reasonWithFeedback.driverCancelEmergency)
        assertEquals(reasonData.driverCancelEmergencyDetail, reasonWithFeedback.driverCancelEmergencyDetail)
        assertEquals(reasonData.feedbackPassengerRef, reasonWithFeedback.feedbackPassengerRef)
        assertEquals(reasonData.feedbackDriverRef, reasonWithFeedback.feedbackDriverRef)
    }
}
