package com.example.xevivuapp.repository

import com.example.xevivuapp.data.PassengerData

object PassengerDataMock {

    val validPassenger = PassengerData(
        user_ID = "12345",
        firstname = "John",
        lastname = "Doe",
        email = "john.doe@example.com",
        gender = true,
        mobile_No = "+1234567890",
        point = 100,
        password = "password123",
        avatar_Link = "https://example.com/avatar.jpg",
        reportPassengerNum = 0,
        bookingTripNum = 5
    )

    val passengerWithMissingInfo = PassengerData(
        user_ID = "67890",
        firstname = "Jane",
        lastname = "Smith",
        email = "jane.smith@example.com",
        gender = null, // Missing gender
        mobile_No = "+0987654321",
        point = 50,
        password = null, // Missing password
        avatar_Link = "https://example.com/avatar2.jpg",
        reportPassengerNum = 1,
        bookingTripNum = 3
    )

    val passengerWithoutEmail = PassengerData(
        user_ID = "54321",
        firstname = "Alice",
        lastname = "Brown",
        email = null, // Missing email
        gender = false,
        mobile_No = "+1122334455",
        point = 30,
        password = "alicepassword",
        avatar_Link = "https://example.com/avatar3.jpg",
        reportPassengerNum = 2,
        bookingTripNum = 8
    )

    val passengerWithOnlyName = PassengerData(
        user_ID = "98765",
        firstname = "Bob",
        lastname = "Williams",
        email = null, // Missing email
        gender = true,
        mobile_No = null, // Missing mobile number
        point = 10,
        password = "bobpassword",
        avatar_Link = null, // Missing avatar link
        reportPassengerNum = 0,
        bookingTripNum = 1
    )

    val passengerWithNegativePoints = PassengerData(
        user_ID = "11111",
        firstname = "Charlie",
        lastname = "Green",
        email = "charlie.green@example.com",
        gender = true,
        mobile_No = "+5544332211",
        point = -5, // Negative points
        password = "charlie123",
        avatar_Link = "https://example.com/avatar4.jpg",
        reportPassengerNum = 0,
        bookingTripNum = 2
    )

    val passengerWithSpecialCharsInName = PassengerData(
        user_ID = "22222",
        firstname = "S@rah",
        lastname = "M!ller",
        email = "sarah.miller@example.com",
        gender = false,
        mobile_No = "+6655443322",
        point = 150,
        password = "specialChars123",
        avatar_Link = "https://example.com/avatar5.jpg",
        reportPassengerNum = 0,
        bookingTripNum = 10
    )
}
