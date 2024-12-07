package com.example.xevivuapp.data_test

import com.example.xevivuapp.data.PassengerSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PassengerSupportTest {

    @Test
    fun `test PassengerSupport default values`() {
        // Arrange
        val passengerSupport = PassengerSupport()

        // Assert
        assertEquals("", passengerSupport.passengerSupport_ID)
        assertEquals("", passengerSupport.passenger_ID)
        assertEquals("", passengerSupport.supportContent)
        assertNull(passengerSupport.supportImgUrl)
        assertEquals("", passengerSupport.createTime)
    }

    @Test
    fun `test PassengerSupport with custom values`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Help needed with booking",
            supportImgUrl = "http://example.com/image.jpg",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Assert
        assertEquals("PS001", passengerSupport.passengerSupport_ID)
        assertEquals("P001", passengerSupport.passenger_ID)
        assertEquals("Help needed with booking", passengerSupport.supportContent)
        assertEquals("http://example.com/image.jpg", passengerSupport.supportImgUrl)
        assertEquals("2024-12-01T10:00:00Z", passengerSupport.createTime)
    }

    @Test
    fun `test PassengerSupport equality with same data`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Support content example",
            createTime = "2024-12-01T10:00:00Z"
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Support content example",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Assert
        assertEquals(passengerSupport1, passengerSupport2)
    }

    @Test
    fun `test PassengerSupport inequality with different data`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001"
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS002",
            passenger_ID = "P002"
        )

        // Assert
        assertNotEquals(passengerSupport1, passengerSupport2)
    }

    @Test
    fun `test PassengerSupport toString includes all fields`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Help needed",
            supportImgUrl = "http://example.com/help.jpg",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Act
        val toStringResult = passengerSupport.toString()

        // Assert
        assertTrue(toStringResult.contains("PS001"))
        assertTrue(toStringResult.contains("P001"))
        assertTrue(toStringResult.contains("Help needed"))
        assertTrue(toStringResult.contains("http://example.com/help.jpg"))
        assertTrue(toStringResult.contains("2024-12-01T10:00:00Z"))
    }

    @Test
    fun `test PassengerSupport immutability`() {
        // Arrange
        val original = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Initial content"
        )

        // Act
        val updated = original.copy(supportContent = "Updated content")

        // Assert
        assertEquals("Initial content", original.supportContent)
        assertEquals("Updated content", updated.supportContent)
    }

    @Test
    fun `test PassengerSupport null supportImgUrl`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "No image provided",
            supportImgUrl = null,
            createTime = "2024-12-01T10:00:00Z"
        )

        // Assert
        assertNull(passengerSupport.supportImgUrl)
        assertEquals("No image provided", passengerSupport.supportContent)
    }

    @Test
    fun `test PassengerSupport with empty fields`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "",
            passenger_ID = "",
            supportContent = "",
            supportImgUrl = null,
            createTime = ""
        )

        // Assert
        assertEquals("", passengerSupport.passengerSupport_ID)
        assertEquals("", passengerSupport.passenger_ID)
        assertEquals("", passengerSupport.supportContent)
        assertNull(passengerSupport.supportImgUrl)
        assertEquals("", passengerSupport.createTime)
    }

    @Test
    fun `test PassengerSupport complex equality with null and non-null fields`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Some content",
            supportImgUrl = null
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Some content",
            supportImgUrl = null
        )

        // Assert
        assertEquals(passengerSupport1, passengerSupport2)
    }

    @Test
    fun `test PassengerSupport when ID and Content are identical but other fields differ`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Need help with booking",
            supportImgUrl = "http://example.com/img1.jpg",
            createTime = "2024-12-01T10:00:00Z"
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Need help with booking",
            supportImgUrl = "http://example.com/img2.jpg",
            createTime = "2024-12-02T10:00:00Z"
        )

        // Assert
        assertNotEquals(passengerSupport1, passengerSupport2)
    }

    @Test
    fun `test PassengerSupport createTime format validation`() {
        // Arrange
        val validTime = "2024-12-01T10:00:00Z"
        val invalidTime = "01-12-2024 10:00:00"

        val passengerSupportValid = PassengerSupport(
            createTime = validTime
        )
        val passengerSupportInvalid = PassengerSupport(
            createTime = invalidTime
        )

        // Assert
        assertEquals(validTime, passengerSupportValid.createTime)
        assertEquals(invalidTime, passengerSupportInvalid.createTime)
    }

    @Test
    fun `test PassengerSupport deep copy creation`() {
        // Arrange
        val original = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Initial content",
            supportImgUrl = "http://example.com/img.jpg",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Act
        val copied = original.copy()

        // Assert
        assertEquals(original, copied)
        assertNotSame(original, copied) // Verify they are distinct objects
    }

    @Test
    fun `test PassengerSupport changes when copying with null fields`() {
        // Arrange
        val original = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Initial content",
            supportImgUrl = "http://example.com/img.jpg",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Act
        val updated = original.copy(supportImgUrl = null)

        // Assert
        assertNotEquals(original.supportImgUrl, updated.supportImgUrl)
        assertNull(updated.supportImgUrl)
        assertEquals(original.passengerSupport_ID, updated.passengerSupport_ID)
    }

    @Test
    fun `test PassengerSupport field length validation`() {
        // Arrange
        val longContent = "A".repeat(500) // Simulating a long string
        val passengerSupport = PassengerSupport(
            supportContent = longContent
        )

        // Assert
        assertTrue(passengerSupport.supportContent.length == 500)
    }

    @Test
    fun `test PassengerSupport for empty and whitespace values`() {
        // Arrange
        val emptyFields = PassengerSupport(
            passengerSupport_ID = "",
            passenger_ID = " ",
            supportContent = "\t",
            createTime = ""
        )

        // Assert
        assertEquals("", emptyFields.passengerSupport_ID)
        assertEquals(" ", emptyFields.passenger_ID)
        assertEquals("\t", emptyFields.supportContent)
        assertEquals("", emptyFields.createTime)
    }

    @Test
    fun `test PassengerSupport hashCode consistency`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Need help"
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Need help"
        )

        // Assert
        assertEquals(passengerSupport1.hashCode(), passengerSupport2.hashCode())
    }

    @Test
    fun `test PassengerSupport different hashCodes for unequal objects`() {
        // Arrange
        val passengerSupport1 = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Need help"
        )
        val passengerSupport2 = PassengerSupport(
            passengerSupport_ID = "PS002",
            passenger_ID = "P002",
            supportContent = "Need help"
        )

        // Assert
        assertNotEquals(passengerSupport1.hashCode(), passengerSupport2.hashCode())
    }

    @Test
    fun `test PassengerSupport for nullability in optional fields`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportImgUrl = null
        )

        // Assert
        assertNull(passengerSupport.supportImgUrl)
        assertNotNull(passengerSupport.passengerSupport_ID)
        assertNotNull(passengerSupport.passenger_ID)
    }

    @Test
    fun `test PassengerSupport toString readability`() {
        // Arrange
        val passengerSupport = PassengerSupport(
            passengerSupport_ID = "PS001",
            passenger_ID = "P001",
            supportContent = "Sample support content",
            createTime = "2024-12-01T10:00:00Z"
        )

        // Act
        val toStringOutput = passengerSupport.toString()

        // Assert
        assertTrue(toStringOutput.contains("PS001"))
        assertTrue(toStringOutput.contains("P001"))
        assertTrue(toStringOutput.contains("Sample support content"))
        assertTrue(toStringOutput.contains("2024-12-01T10:00:00Z"))
    }
}
