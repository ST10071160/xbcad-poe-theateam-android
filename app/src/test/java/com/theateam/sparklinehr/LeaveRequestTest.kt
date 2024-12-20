package com.theateam.sparklinehr

import android.content.SharedPreferences
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LeaveRequestTest {
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockDbRef: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    @Before
    fun setUp() {
        // Mock dependencies
        mockSharedPreferences = mock(SharedPreferences::class.java)
        firebaseDatabase = mock(FirebaseDatabase::class.java)
        mockDbRef = mock(DatabaseReference::class.java)

        // Mock SharedPreferences behavior
        `when`(mockSharedPreferences.getString("EMPLOYEE_ID", null)).thenReturn("user123")

        // Mock Firebase references
        `when`(firebaseDatabase.reference).thenReturn(mockDbRef)
    }

    @Test
    fun `test leave request is written to Firebase`() {
        val mockChildReference = mock(DatabaseReference::class.java)
        val mockTask = mock(Task::class.java) as Task<Void>

        `when`(mockDbRef.child("Pending Leave Requests")).thenReturn(mockChildReference)
        `when`(mockChildReference.child("user123,2024-01-01")).thenReturn(mockChildReference)
        `when`(mockChildReference.setValue(any())).thenReturn(mockTask)

        val latch = CountDownLatch(1)
        val captor = ArgumentCaptor.forClass(OnSuccessListener::class.java) as ArgumentCaptor<OnSuccessListener<Void>>
        `when`(mockTask.addOnSuccessListener(captor.capture())).thenAnswer {
            captor.value.onSuccess(null) // Trigger the captured OnSuccessListener
            latch.countDown() // Signal success
            mockTask
        }


        val leaveRequestHandler = LeaveRequestHandler(mockSharedPreferences, firebaseDatabase)
        leaveRequestHandler.writeToFirebase("sick_leave", "2024-01-01", "2024-01-05", "doc.pdf")

        latch.await(2, TimeUnit.SECONDS) // Wait for the async operation to complete

        verify(mockChildReference).setValue(any())
    }


    class LeaveRequestHandler(
        private val sharedPreferences: SharedPreferences,
        private val firebaseDatabase: FirebaseDatabase
    ) {
        fun writeToFirebase(leaveType: String, fromDate: String, toDate: String, document: String) {
            val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()
            val leaveReqKey = "${userNum},${fromDate}"
            val dbRef = firebaseDatabase.reference

            val entry = LeaveRequest(leaveType, fromDate, toDate, document)
            dbRef.child("Pending Leave Requests").child(leaveReqKey).setValue(entry)
                .addOnSuccessListener {
                    // Log success
                }
                .addOnFailureListener { exception ->
                    // Log failure
                }
        }
    }

    data class LeaveRequest(val leaveType: String, val fromDate: String, val toDate: String, val document: String)
}