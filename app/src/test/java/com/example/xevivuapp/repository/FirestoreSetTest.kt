package com.example.xevivuapp.repository

import com.example.xevivuapp.data.PassengerData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.jupiter.api.Test

class FirestoreSetTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var mockPassengerData: PassengerData

    @Before
    fun setup() {
        // Mock Firestore và CollectionReference
        firestore = mockk()
        collection = mockk()

        // Mock method gọi firestore.collection()
        every { firestore.collection(any()) } returns collection

        // Mock set() method
        every { collection.document(any()).set(any(), any()) } returns mockk()

        // Khởi tạo dữ liệu mock
        mockPassengerData = PassengerDataMock.validPassenger
    }

    @Test
    fun testSetPassengerData() {
        // Arrange
        val userId = "12345" // Sử dụng ID của người dùng mock
        val expectedPassengerData = PassengerDataMock.validPassenger

        // Act
        collection.document(userId).set(expectedPassengerData)

        // Assert
        verify { collection.document(userId).set(expectedPassengerData, SetOptions.merge()) }
    }

    @Test
    fun testSetPassengerDataWithMissingInfo() {
        // Arrange
        val userId = "67890"
        val passengerWithMissingInfo = PassengerDataMock.passengerWithMissingInfo

        // Act
        collection.document(userId).set(passengerWithMissingInfo)

        // Assert
        verify { collection.document(userId).set(passengerWithMissingInfo, SetOptions.merge()) }
    }

    @Test
    fun testSetPassengerDataWithoutEmail() {
        // Arrange
        val userId = "54321"
        val passengerWithoutEmail = PassengerDataMock.passengerWithoutEmail

        // Act
        collection.document(userId).set(passengerWithoutEmail)

        // Assert
        verify { collection.document(userId).set(passengerWithoutEmail, SetOptions.merge()) }
    }

    @Test
    fun testSetPassengerDataWithOnlyName() {
        // Arrange
        val userId = "98765"
        val passengerWithOnlyName = PassengerDataMock.passengerWithOnlyName

        // Act
        collection.document(userId).set(passengerWithOnlyName)

        // Assert
        verify { collection.document(userId).set(passengerWithOnlyName, SetOptions.merge()) }
    }

    @Test
    fun testSetPassengerDataWithNegativePoints() {
        // Arrange
        val userId = "11111"
        val passengerWithNegativePoints = PassengerDataMock.passengerWithNegativePoints

        // Act
        collection.document(userId).set(passengerWithNegativePoints)

        // Assert
        verify { collection.document(userId).set(passengerWithNegativePoints, SetOptions.merge()) }
    }

    @Test
    fun testSetPassengerDataWithSpecialCharsInName() {
        // Arrange
        val userId = "22222"
        val passengerWithSpecialCharsInName = PassengerDataMock.passengerWithSpecialCharsInName

        // Act
        collection.document(userId).set(passengerWithSpecialCharsInName)

        // Assert
        verify { collection.document(userId).set(passengerWithSpecialCharsInName, SetOptions.merge()) }
    }
}
