package com.example.xevivuapp.repository

import com.example.xevivuapp.data.DriverFeedback

object DriverFeedbackMock {

    val validFeedback = DriverFeedback(
        driverFeedback_ID = "feedback12345",
        driver_ID = "driver123",
        passenger_ID = "passenger123",
        star = 5,
        feedback = "Excellent driver, very polite and professional.",
        reportDriverReason = "None",
        reportDriverReasonDetail = "No issues reported.",
        driverFeedbackTime = "2024-12-01T14:30:00"
    )

    val feedbackWithLowRating = DriverFeedback(
        driverFeedback_ID = "feedback67890",
        driver_ID = "driver456",
        passenger_ID = "passenger456",
        star = 2,
        feedback = "Driver was late and vehicle was not clean.",
        reportDriverReason = "Poor vehicle condition",
        reportDriverReasonDetail = "The car was not clean and had an unpleasant smell.",
        driverFeedbackTime = "2024-12-02T16:00:00"
    )

    val feedbackWithNoReport = DriverFeedback(
        driverFeedback_ID = "feedback11223",
        driver_ID = "driver789",
        passenger_ID = "passenger789",
        star = 4,
        feedback = "Good ride, but the driver could have been more friendly.",
        reportDriverReason = "",
        reportDriverReasonDetail = "",
        driverFeedbackTime = "2024-12-03T12:00:00"
    )

    val feedbackWithNegativeStar = DriverFeedback(
        driverFeedback_ID = "feedback33445",
        driver_ID = "driver101",
        passenger_ID = "passenger101",
        star = -1,  // Invalid star value
        feedback = "Worst experience ever, the driver was rude and unprofessional.",
        reportDriverReason = "Driver was rude",
        reportDriverReasonDetail = "The driver was not polite and ignored our requests.",
        driverFeedbackTime = "2024-12-04T18:00:00"
    )

    val feedbackWithMissingFields = DriverFeedback(
        driverFeedback_ID = "feedback55667",
        driver_ID = "driver202",
        passenger_ID = "passenger202",
        star = 3,
        feedback = "The ride was fine but the vehicle was not up to expectations.",
        reportDriverReason = "Vehicle condition",
        reportDriverReasonDetail = "The car had some issues with the air conditioning.",
        driverFeedbackTime = ""  // Missing time
    )

    val feedbackWithEmptyFields = DriverFeedback(
        driverFeedback_ID = "",
        driver_ID = "",
        passenger_ID = "",
        star = 0,  // Default star value
        feedback = "",
        reportDriverReason = "",
        reportDriverReasonDetail = "",
        driverFeedbackTime = ""  // Empty feedback data
    )
}
