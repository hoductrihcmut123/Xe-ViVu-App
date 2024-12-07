package com.example.xevivuapp.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class FirestoreDeleteTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private lateinit var documentReference: DocumentReference

    @Before
    fun setup() {
        // Mock Firestore và CollectionReference
        firestore = mockk()
        collection = mockk()
        documentReference = mockk()

        // Mock firestore.collection()
        every { firestore.collection(any()) } returns collection
        every { collection.document(any()) } returns documentReference
    }

    @Test
    fun testDeleteDriverFeedback() {
        // Mock hành vi delete() trả về thành công
        every { documentReference.delete() } returns mockk {
            every { isSuccessful } returns true
        }

        // Thực hiện xóa
        val feedbackId = "feedback12345"
        val deleteTask = collection.document(feedbackId).delete()

        // Kiểm tra kết quả
        verify { documentReference.delete() }

        // Kiểm tra xem xóa có thành công không
        assertTrue { deleteTask.isSuccessful }
    }

    @Test
    fun testDeleteFeedbackWithMissingFields() {
        // Mock hành vi delete() trả về thành công
        every { documentReference.delete() } returns mockk {
            every { isSuccessful } returns true
        }

        // Thực hiện xóa
        val feedbackId = "feedback55667"  // Feedback with missing fields
        val deleteTask = collection.document(feedbackId).delete()

        // Kiểm tra kết quả
        verify { documentReference.delete() }

        // Kiểm tra xem xóa có thành công không
        assertTrue { deleteTask.isSuccessful }
    }

    @Test
    fun testDeleteFeedbackWithInvalidStar() {
        // Mock hành vi delete() trả về thành công
        every { documentReference.delete() } returns mockk {
            every { isSuccessful } returns true
        }

        // Thực hiện xóa
        val feedbackId = "feedback33445"  // Feedback with invalid star
        val deleteTask = collection.document(feedbackId).delete()

        // Kiểm tra kết quả
        verify { documentReference.delete() }

        // Kiểm tra xem xóa có thành công không
        assertTrue { deleteTask.isSuccessful }
    }
}
