package com.example.xevivuapp.data_test

import com.example.xevivuapp.data.PassengerData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PassengerDataTest {

    @Test
    fun `test PassengerData initialization`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            gender = true,
            mobile_No = "1234567890",
            point = 100,
            password = "password123",
            avatar_Link = "http://example.com/avatar.jpg",
            reportPassengerNum = 3,
            bookingTripNum = 10
        )

        // Assert
        assertEquals("123", passengerData.user_ID)
        assertEquals("John", passengerData.firstname)
        assertEquals("Doe", passengerData.lastname)
        assertEquals("john.doe@example.com", passengerData.email)
        assertEquals(true, passengerData.gender)
        assertEquals("1234567890", passengerData.mobile_No)
        assertEquals(100, passengerData.point)
        assertEquals("password123", passengerData.password)
        assertEquals("http://example.com/avatar.jpg", passengerData.avatar_Link)
        assertEquals(3, passengerData.reportPassengerNum)
        assertEquals(10, passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData equality`() {
        // Arrange
        val passengerData1 = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe"
        )
        val passengerData2 = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe"
        )
        val passengerData3 = PassengerData(
            user_ID = "456",
            firstname = "Jane",
            lastname = "Smith"
        )

        // Assert
        assertEquals(passengerData1, passengerData2) // Equal objects
        assertNotEquals(passengerData1, passengerData3) // Different objects
    }

    @Test
    fun `test PassengerData copy`() {
        // Arrange
        val original = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            gender = true
        )

        // Act
        val copy = original.copy(email = "new.email@example.com")

        // Assert
        assertEquals("123", copy.user_ID)
        assertEquals("John", copy.firstname)
        assertEquals("Doe", copy.lastname)
        assertEquals("new.email@example.com", copy.email) // Updated value
        assertEquals(true, copy.gender)
        assertNotEquals(original.email, copy.email) // Ensure copy is modified
    }

    @Test
    fun `test PassengerData default values`() {
        // Arrange
        val passengerData = PassengerData()

        // Assert
        assertEquals(null, passengerData.user_ID)
        assertEquals(null, passengerData.firstname)
        assertEquals(null, passengerData.lastname)
        assertEquals(null, passengerData.email)
        assertEquals(null, passengerData.gender)
        assertEquals(null, passengerData.mobile_No)
        assertEquals(null, passengerData.point)
        assertEquals(null, passengerData.password)
        assertEquals(null, passengerData.avatar_Link)
        assertEquals(null, passengerData.reportPassengerNum)
        assertEquals(null, passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData with null fields`() {
        // Arrange
        val passengerData = PassengerData()

        // Assert
        assertNull(passengerData.user_ID)
        assertNull(passengerData.firstname)
        assertNull(passengerData.lastname)
        assertNull(passengerData.email)
        assertNull(passengerData.gender)
        assertNull(passengerData.mobile_No)
        assertNull(passengerData.point)
        assertNull(passengerData.password)
        assertNull(passengerData.avatar_Link)
        assertNull(passengerData.reportPassengerNum)
        assertNull(passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData with partial fields`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            email = "alice@example.com"
        )

        // Assert
        assertEquals("001", passengerData.user_ID)
        assertEquals("Alice", passengerData.firstname)
        assertNull(passengerData.lastname)
        assertEquals("alice@example.com", passengerData.email)
        assertNull(passengerData.gender)
    }

    @Test
    fun `test PassengerData hashCode consistency`() {
        // Arrange
        val passengerData1 = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            email = "alice@example.com"
        )
        val passengerData2 = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            email = "alice@example.com"
        )

        // Assert
        assertEquals(passengerData1.hashCode(), passengerData2.hashCode())
    }

    @Test
    fun `test PassengerData unequal objects`() {
        // Arrange
        val passengerData1 = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            email = "alice@example.com"
        )
        val passengerData2 = PassengerData(
            user_ID = "002",
            firstname = "Bob",
            email = "bob@example.com"
        )

        // Assert
        assertNotEquals(passengerData1, passengerData2)
    }

    @Test
    fun `test PassengerData copy with all fields`() {
        // Arrange
        val original = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            gender = true,
            mobile_No = "0987654321",
            point = 200,
            password = "secure123",
            avatar_Link = "http://example.com/avatar.png",
            reportPassengerNum = 2,
            bookingTripNum = 5
        )

        // Act
        val copy = original.copy(
            firstname = "Jane",
            email = "jane.doe@example.com"
        )

        // Assert
        assertEquals("123", copy.user_ID)
        assertEquals("Jane", copy.firstname) // Updated field
        assertEquals("Doe", copy.lastname)
        assertEquals("jane.doe@example.com", copy.email) // Updated field
        assertEquals(true, copy.gender)
        assertEquals("0987654321", copy.mobile_No)
        assertEquals(200, copy.point)
        assertEquals("secure123", copy.password)
        assertEquals("http://example.com/avatar.png", copy.avatar_Link)
        assertEquals(2, copy.reportPassengerNum)
        assertEquals(5, copy.bookingTripNum)
    }

    @Test
    fun `test PassengerData default copy`() {
        // Arrange
        val passengerData = PassengerData()

        // Act
        val copy = passengerData.copy()

        // Assert
        assertEquals(passengerData, copy)
    }

    @Test
    fun `test PassengerData toString`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            lastname = "Smith",
            email = "alice.smith@example.com"
        )

        // Act
        val stringRepresentation = passengerData.toString()

        // Assert
        assertTrue(stringRepresentation.contains("user_ID=001"))
        assertTrue(stringRepresentation.contains("firstname=Alice"))
        assertTrue(stringRepresentation.contains("lastname=Smith"))
        assertTrue(stringRepresentation.contains("email=alice.smith@example.com"))
    }

    @Test
    fun `test PassengerData with negative values`() {
        // Arrange
        val passengerData = PassengerData(
            point = -10,
            reportPassengerNum = -5,
            bookingTripNum = -3
        )

        // Assert
        assertEquals(-10, passengerData.point)
        assertEquals(-5, passengerData.reportPassengerNum)
        assertEquals(-3, passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData immutability`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "001",
            firstname = "Alice"
        )

        // Act & Assert
        // Data classes are immutable; cannot modify fields directly
        val updatedData = passengerData.copy(firstname = "Bob")
        assertEquals("Alice", passengerData.firstname) // Original remains unchanged
        assertEquals("Bob", updatedData.firstname) // New object with updated value
    }

    @Test
    fun `test PassengerData equality with different instances`() {
        // Arrange
        val passengerData1 = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe"
        )
        val passengerData2 = PassengerData(
            user_ID = "123",
            firstname = "John",
            lastname = "Doe"
        )

        // Assert
        assertEquals(passengerData1, passengerData2)
        assertTrue(passengerData1 == passengerData2)
    }

    @Test
    fun `test PassengerData inequality with different IDs`() {
        // Arrange
        val passengerData1 = PassengerData(
            user_ID = "123",
            firstname = "John"
        )
        val passengerData2 = PassengerData(
            user_ID = "456",
            firstname = "John"
        )

        // Assert
        assertNotEquals(passengerData1, passengerData2)
    }

    @Test
    fun `test PassengerData fields with special characters`() {
        // Arrange
        val passengerData = PassengerData(
            firstname = "J@hn!",
            lastname = "D&oe#",
            email = "johndoe@example.com"
        )

        // Assert
        assertEquals("J@hn!", passengerData.firstname)
        assertEquals("D&oe#", passengerData.lastname)
    }

    @Test
    fun `test PassengerData numeric fields`() {
        // Arrange
        val passengerData = PassengerData(
            point = 1000,
            reportPassengerNum = 5,
            bookingTripNum = 25
        )

        // Assert
        assertEquals(1000, passengerData.point)
        assertEquals(5, passengerData.reportPassengerNum)
        assertEquals(25, passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData large numeric fields`() {
        // Arrange
        val passengerData = PassengerData(
            point = Int.MAX_VALUE,
            reportPassengerNum = Int.MAX_VALUE,
            bookingTripNum = Int.MAX_VALUE
        )

        // Assert
        assertEquals(Int.MAX_VALUE, passengerData.point)
        assertEquals(Int.MAX_VALUE, passengerData.reportPassengerNum)
        assertEquals(Int.MAX_VALUE, passengerData.bookingTripNum)
    }

    @Test
    fun `test PassengerData toString includes all fields`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "001",
            firstname = "Alice",
            lastname = "Smith",
            email = "alice.smith@example.com",
            point = 100,
            reportPassengerNum = 3,
            bookingTripNum = 7
        )

        // Act
        val toStringResult = passengerData.toString()

        // Assert
        assertTrue(toStringResult.contains("user_ID=001"))
        assertTrue(toStringResult.contains("firstname=Alice"))
        assertTrue(toStringResult.contains("lastname=Smith"))
        assertTrue(toStringResult.contains("email=alice.smith@example.com"))
        assertTrue(toStringResult.contains("point=100"))
        assertTrue(toStringResult.contains("reportPassengerNum=3"))
        assertTrue(toStringResult.contains("bookingTripNum=7"))
    }

    @Test
    fun `test PassengerData with null values`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = null,
            firstname = null,
            lastname = null,
            email = null
        )

        // Assert
        assertNull(passengerData.user_ID)
        assertNull(passengerData.firstname)
        assertNull(passengerData.lastname)
        assertNull(passengerData.email)
    }

    @Test
    fun `test PassengerData when all fields are empty strings`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "",
            firstname = "",
            lastname = "",
            email = ""
        )

        // Assert
        assertEquals("", passengerData.user_ID)
        assertEquals("", passengerData.firstname)
        assertEquals("", passengerData.lastname)
        assertEquals("", passengerData.email)
    }

    @Test
    fun `test PassengerData immutability of numeric fields`() {
        // Arrange
        val passengerData = PassengerData(
            point = 100,
            reportPassengerNum = 10,
            bookingTripNum = 5
        )

        // Act
        val updatedPassengerData = passengerData.copy(point = 200)

        // Assert
        assertEquals(100, passengerData.point) // Original remains unchanged
        assertEquals(200, updatedPassengerData.point) // New object reflects updated value
    }

    @Test
    fun `test PassengerData with null and non-null mix`() {
        // Arrange
        val passengerData = PassengerData(
            user_ID = "123",
            firstname = null,
            lastname = "Doe",
            email = null,
            point = 50
        )

        // Assert
        assertEquals("123", passengerData.user_ID)
        assertNull(passengerData.firstname)
        assertEquals("Doe", passengerData.lastname)
        assertNull(passengerData.email)
        assertEquals(50, passengerData.point)
    }
}
