package com.example.xevivuapp.util_functions_test

import com.example.xevivuapp.common.utils.Utils.extractTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ExtractTimeTest {

    @Test
    fun `test extractTime with valid input`() {
        // Arrange
        val inputDate = "Fri Dec 01 12:34:56 GMT 2023"
        val expectedOutput = "01/12/2023, 19:34:56"

        // Act
        val result = inputDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with invalid input`() {
        // Arrange
        val invalidDate = "invalid_date_string"

        // Act
        val result = invalidDate.extractTime()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `test extractTime with null-like input`() {
        // Arrange
        val nullInput = ""

        // Act
        val result = nullInput.extractTime()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `test extractTime with different time zones`() {
        // Arrange
        val inputDate = "Fri Dec 01 12:34:56 PST 2023" // PST timezone
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("PST")
        val parsedDate = inputFormat.parse(inputDate)

        val expectedOutput = if (parsedDate != null) {
            val outputFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH)
            outputFormat.format(parsedDate)
        } else {
            ""
        }

        // Act
        val result = inputDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with null date object`() {
        // Arrange
        val inputDate = "Thu Jan 01 00:00:00 GMT 1970" // Valid edge case date
        val expectedOutput = "01/01/1970, 08:00:00"

        // Act
        val result = inputDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with leap year date`() {
        // Arrange
        val leapYearDate = "Tue Feb 29 12:34:56 GMT 2024" // Leap year date
        val expectedOutput = "29/02/2024, 19:34:56"

        // Act
        val result = leapYearDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with very old date`() {
        // Arrange
        val oldDate = "Fri Jan 01 00:00:00 GMT 1800"
        val expectedOutput = "01/01/1800, 07:00:00"

        // Act
        val result = oldDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with future date`() {
        // Arrange
        val futureDate = "Wed Dec 31 23:59:59 GMT 3000"
        val expectedOutput = "01/01/3001, 06:59:59"

        // Act
        val result = futureDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with minimum valid date string`() {
        // Arrange
        val minValidDate = "Mon Mar 1 00:00:00 GMT 2000"
        val expectedOutput = "01/03/2000, 07:00:00"

        // Act
        val result = minValidDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with additional spaces in input`() {
        // Arrange
        val spacedDate = "   Fri Dec 01 12:34:56 GMT 2023   "
        val expectedOutput = "01/12/2023, 19:34:56"

        // Act
        val result = spacedDate.trim().extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with missing time zone in input`() {
        // Arrange
        val noTimezoneDate = "Fri Dec 01 12:34:56 2023"
        val expectedOutput = ""

        // Act
        val result = noTimezoneDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with non-English locale input`() {
        // Arrange
        val nonEnglishLocaleDate = "金 12月 01 12:34:56 GMT 2023" // Japanese format
        val expectedOutput = ""

        // Act
        val result = nonEnglishLocaleDate.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with very long input`() {
        // Arrange
        val longInput = "Fri Dec 01 12:34:56 GMT 2023".repeat(1000)
        val expectedOutput = "01/12/2023, 19:34:56"

        // Act
        val result = longInput.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with non-date input`() {
        // Arrange
        val randomText = "This is not a date!"
        val expectedOutput = ""

        // Act
        val result = randomText.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `test extractTime with numeric input`() {
        // Arrange
        val numericInput = "1234567890"
        val expectedOutput = ""

        // Act
        val result = numericInput.extractTime()

        // Assert
        assertEquals(expectedOutput, result)
    }
}
