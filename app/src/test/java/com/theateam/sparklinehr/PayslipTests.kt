import android.graphics.pdf.PdfDocument
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.mockito.Mockito.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PayslipTests {

    // Mock FirebaseDatabase and its dependencies
    @Test
    fun `test payslips are retrieved for the correct user from Firebase`() {
        val mockDatabase = mock(FirebaseDatabase::class.java)
        val mockDbRef = mock(DatabaseReference::class.java)
        val mockUserRef = mock(DatabaseReference::class.java)
        val mockDataSnapshot = mock(DataSnapshot::class.java)
        val mockChildSnapshot1 = mock(DataSnapshot::class.java)
        val mockChildSnapshot2 = mock(DataSnapshot::class.java)

        `when`(mockDatabase.getReference("SparkLineHR")).thenReturn(mockDbRef)
        `when`(mockDbRef.child("payslips")).thenReturn(mockDbRef)
        `when`(mockDbRef.child("SPL00001")).thenReturn(mockUserRef)

        val mockPayslip1 = Payslip(
            company = "SparkLine",
            empName = "Aidan Schwoerer",
            empNum = "SPL00001",
            empPos = "Internal Auditor",
            grossSalary = "70000",
            issueDate = "2024-11-22",
            payslipPeriod = "2024-04-01 - 2024-04-30",
            pensionPercent = "5",
            taxNum = "0987654321",
            uifPercent = "1"
        )
        val mockPayslip2 = Payslip(
            company = "SparkLine",
            empName = "Aidan Schwoerer",
            empNum = "SPL00001",
            empPos = "Internal Auditor",
            grossSalary = "70000",
            issueDate = "2024-11-23",
            payslipPeriod = "2024-07-01 - 2024-07-31",
            pensionPercent = "5",
            taxNum = "0987654321",
            uifPercent = "1"
        )

        `when`(mockDataSnapshot.exists()).thenReturn(true)
        `when`(mockDataSnapshot.children).thenReturn(listOf(mockChildSnapshot1, mockChildSnapshot2))

        `when`(mockChildSnapshot1.getValue(Payslip::class.java)).thenReturn(mockPayslip1)
        `when`(mockChildSnapshot2.getValue(Payslip::class.java)).thenReturn(mockPayslip2)

        val latch = CountDownLatch(1)
        doAnswer { invocation ->
            val listener = invocation.arguments[0] as ValueEventListener
            listener.onDataChange(mockDataSnapshot)
            latch.countDown() // Signal when callback is complete
            null
        }.`when`(mockUserRef).addListenerForSingleValueEvent(any(ValueEventListener::class.java))

        val payslipList = mutableListOf<Payslip>()
        mockUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (childSnapshot in dataSnapshot.children) {
                        val payslip = childSnapshot.getValue(Payslip::class.java)
                        if (payslip != null) {
                            payslipList.add(payslip)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        latch.await(2, TimeUnit.SECONDS) // Wait for the async operation to complete
        assertEquals(2, payslipList.size)
        assertTrue(payslipList.contains(mockPayslip1))
        assertTrue(payslipList.contains(mockPayslip2))
    }


    data class Payslip(
        val company: String = "",
        val empName: String = "",
        val empNum: String = "",
        val empPos: String = "",
        val grossSalary: String = "",
        val issueDate: String = "",
        val payslipPeriod: String = "",
        val pensionPercent: String = "",
        val taxNum: String = "",
        val uifPercent: String = ""
    )
}
