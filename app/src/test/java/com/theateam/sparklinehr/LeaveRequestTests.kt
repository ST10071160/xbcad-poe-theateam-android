import android.content.SharedPreferences
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.theateam.sparklinehr.LeaveRequestActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LeaveRequestTests {

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
        // Mock Firebase database and task behavior
        val mockChildReference = mock(DatabaseReference::class.java)
        val mockTask = mock(Task::class.java) as Task<Void>

        // Mock child() and setValue()
        `when`(mockDbRef.child("Pending Leave Requests")).thenReturn(mockChildReference)
        `when`(mockChildReference.child("user123,2024-01-01")).thenReturn(mockChildReference)
        `when`(mockChildReference.setValue(any())).thenReturn(mockTask)

        // ArgumentCaptor for OnSuccessListener
        val captor = ArgumentCaptor.forClass(OnSuccessListener::class.java) as ArgumentCaptor<OnSuccessListener<Void>>
        `when`(mockTask.addOnSuccessListener(captor.capture())).thenAnswer {
            captor.value.onSuccess(null) // Trigger success callback
            mockTask
        }

        // Call the handler
        val leaveRequestHandler = LeaveRequestHandler(mockSharedPreferences, firebaseDatabase)
        leaveRequestHandler.writeToFirebase("sick_leave", "2024-01-01", "2024-01-05", "doc.pdf")

        // Verify interactions
        verify(mockChildReference).setValue(any())
        assertNotNull(captor.value) // Ensure the listener was captured and invoked
    }


}


class LeaveRequestHandler(
    private val sharedPreferences: SharedPreferences,
    private val firebaseDatabase: FirebaseDatabase
) {
    fun writeToFirebase(leaveType: String, fromDate: String, toDate: String, document: String) {
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()
        val leaveReqKey = "${userNum},${fromDate}"
        val dbRef = firebaseDatabase.reference

        val entry = LeaveRequestActivity.LeaveRequest(leaveType, fromDate, toDate, document)
        dbRef.child("Pending Leave Requests").child(leaveReqKey).setValue(entry)
            .addOnSuccessListener {
                // Log success
            }
            .addOnFailureListener { exception ->
                // Log failure
            }
    }
}

