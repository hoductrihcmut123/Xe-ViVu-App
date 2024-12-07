package com.example.xevivuapp.util_functions_test

import com.example.xevivuapp.common.utils.Utils.toDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ToDateTest {

    @Test
    fun `test toDate with valid date`() {
        // Arrange
        val validDateString = "Mon Dec 01 12:34:56 GMT 2023"
        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(validDateString)

        // Act
        val result = validDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    @Test
    fun `test toDate with invalid date format`() {
        // Arrange
        val invalidDateString = "2023-12-01 12:34:56"

        // Act
        val result = invalidDateString.toDate()

        // Assert
        assertNull(result)
    }

    @Test
    fun `test toDate with empty string`() {
        // Arrange
        val emptyDateString = ""

        // Act
        val result = emptyDateString.toDate()

        // Assert
        assertNull(result)
    }

    @Test
    fun `test toDate with null input`() {
        // Arrange
        val nullDateString: String? = null

        // Act
        val result = nullDateString?.toDate()

        // Assert
        assertNull(result)
    }

    @Test
    fun `test toDate with leap year date`() {
        // Arrange
        val leapYearDateString = "Mon Feb 29 12:34:56 GMT 2024" // Leap year date
        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(leapYearDateString)

        // Act
        val result = leapYearDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    @Test
    fun `test toDate with future date`() {
        // Arrange
        val futureDateString = "Mon Dec 31 23:59:59 GMT 3000" // Future date
        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(futureDateString)

        // Act
        val result = futureDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    @Test
    fun `test toDate with non-English input`() {
        // Arrange
        val nonEnglishDateString = "金 12月 01 12:34:56 GMT 2023" // Japanese format

        // Act
        val result = nonEnglishDateString.toDate()

        // Assert
        assertNull(result)
    }

    // Trường hợp ngày tháng hợp lệ
    @Test
    fun `test toDate with valid formatted date string (UK format)`() {
        // Arrange
        val validUkDateString = "Mon Dec 01 12:34:56 GMT 2023" // Định dạng ngày chuẩn Anh
        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(validUkDateString)

        // Act
        val result = validUkDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    // Trường hợp ngày tháng hợp lệ với ngày trong tuần viết tắt khác
    @Test
    fun `test toDate with valid abbreviated weekday name`() {
        // Arrange
        val validDateString = "Tue Dec 01 12:34:56 GMT 2023" // Thứ ba viết tắt
        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(validDateString)

        // Act
        val result = validDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    // Trường hợp đầu vào là chuỗi không đúng định dạng
    @Test
    fun `test toDate with incorrectly formatted date string (missing year)`() {
        // Arrange
        val invalidDateString = "Mon Dec 01 12:34:56 GMT" // Thiếu năm

        // Act
        val result = invalidDateString.toDate()

        // Assert
        assertNull(result)
    }

    // Trường hợp có thời gian không hợp lệ
    @Test
    fun `test toDate with invalid time part (invalid time)`() {
        // Arrange
        val invalidTimeString = "Mon Dec 01 25:61:00 GMT 2023" // Thời gian không hợp lệ (25 giờ)

        // Act
        val result = invalidTimeString.toDate()

        // Assert
        assertNull(result)
    }

    // Trường hợp định dạng thời gian hợp lệ nhưng chuỗi có ngày sai định dạng
    @Test
    fun `test toDate with valid time but invalid date format`() {
        // Arrange
        val invalidDateFormatString = "2023-12-01 12:34:56" // Định dạng khác, không phù hợp

        // Act
        val result = invalidDateFormatString.toDate()

        // Assert
        assertNull(result)
    }

    // Trường hợp chuỗi chứa ngày tháng hợp lệ nhưng với ký tự đặc biệt (space, dấu gạch ngang)
    @Test
    fun `test toDate with valid date but containing special characters`() {
        // Arrange
        val specialCharDateString = "Mon Dec 01 12:34:56 GMT+03:00 2023" // Dấu gạch ngang trong timezone

        // Act
        val result = specialCharDateString.toDate()

        // Assert
        assertNotNull(result)
    }

    // Trường hợp ngày tháng đúng nhưng có múi giờ khác
    @Test
    fun `test toDate with valid date and different timezone`() {
        // Arrange
        val validDateWithTimezoneString = "Mon Dec 01 12:34:56 GMT+05:30 2023" // Múi giờ +5:30 (Ấn Độ)

        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(validDateWithTimezoneString)

        // Act
        val result = validDateWithTimezoneString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }

    // Trường hợp định dạng ngày tháng khác, ví dụ: tháng 3 viết đầy đủ
    @Test
    fun `test toDate with full month name format`() {
        // Arrange
        val fullMonthDateString = "Mon March 01 12:34:56 GMT 2023" // Tháng 3 viết đầy đủ

        val expectedDate = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            .parse(fullMonthDateString)

        // Act
        val result = fullMonthDateString.toDate()

        // Assert
        assertNotNull(result)
        assertEquals(expectedDate, result)
    }
}
