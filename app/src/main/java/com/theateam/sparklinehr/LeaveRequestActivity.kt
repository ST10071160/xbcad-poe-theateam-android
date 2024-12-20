package com.theateam.sparklinehr

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theateam.sparklinehr.ViewPersonalInformationActivity.JobDetails
import com.theateam.sparklinehr.databinding.ActivityLeaveRequestBinding
import java.util.Calendar

class LeaveRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaveRequestBinding


    private lateinit var passFrom: String
    private lateinit var passTo: String


    private lateinit var fileUri: Uri
    private lateinit var storageRef: StorageReference


    val leaveTypesList = listOf(
        "Sick",
        "Annual",
        "Compassionate",
        "Family Responsibility",
        "Study"
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLeaveRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_leave_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (binding.leaveRequestLeaveTypeSpinner != null) {

            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, leaveTypesList)

//            arrayAdapter.notifyDataSetChanged()

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.leaveRequestLeaveTypeSpinner.adapter = arrayAdapter

            binding.leaveRequestLeaveTypeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Toast.makeText(this@LeaveRequestActivity,"You have selected " + (leaveTypesList)[position], Toast.LENGTH_SHORT).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.leaveRequestStartDateTextView.setOnClickListener() {
            showDatePicker(binding.leaveRequestStartDateTextView)
        }

        binding.leaveRequestEndDateTextView.setOnClickListener() {
            showDatePicker(binding.leaveRequestEndDateTextView)
        }

        binding.leaveRequestIfMedicalTextView.setOnClickListener() {
            selectDocument()
        }

        binding.leaveRequestApplyLeaveBtn.setOnClickListener() {
            passFrom = binding.leaveRequestStartDateTextView.text.toString()
            passTo = binding.leaveRequestEndDateTextView.text.toString()

            val leaveTypeString = binding.leaveRequestLeaveTypeSpinner.selectedItem.toString()

            if (leaveTypeString.isEmpty() || passFrom.isEmpty() || passTo.isEmpty()) {
                Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
            }
            else if(leaveTypeString == "Sick") {
                if (::fileUri.isInitialized) {
                    createLeaveReq(leaveTypeString, passFrom, passTo)

                    // Display to alert the user that the leave request has been added to the application
                    Toast.makeText(this, "Leave Request Submitted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Document not selected", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                // Change the N/a to null for firebase retrieval into the webb app
                writeToFirebase(leaveTypeString, passFrom, passTo, "")
                Toast.makeText(this, "Leave Request Submitted", Toast.LENGTH_LONG).show()
            }
        }

    }


    //this will create the leave request object and store the entered file into the firebase cloud storage
    private fun createLeaveReq(leaveType: String, fromDate: String, toDate: String) {
        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)


        val fileName = "$userNum,$fromDate"
        storageRef = FirebaseStorage.getInstance().getReference("leaverequest-docs/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val document = uri.toString()
                    writeToFirebase(leaveType, fromDate, toDate, document)
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.e("CreateLeaveRequest", "Failed to get download URL", exception)
                }
                Toast.makeText(this, "Successfully Uploaded File", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("CreateLeaveRequest", "Upload failed", exception)
            }
    }

    //this method will bring up the file selection screen in the user's phone
    private fun selectDocument() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    //this will get the fileUri information based on what the user has selected
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data!!
        }
    }

    //this will write the leave request to the Pending Leave Requests table in the firebase realtime database, and will include either a link to the document in the cloud storage, or the label "N/a"
    private fun writeToFirebase(leaveType: String, fromDate: String, toDate: String, document: String) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val leaveReqKey = "${userNum},${fromDate}"

        val database = Firebase.database
        val dbRef = database.getReference("SparkLineHR")
        val entry = LeaveRequest(leaveType, fromDate, toDate, document)

        dbRef.child("Pending Leave Requests").child(leaveReqKey).setValue(entry)
            .addOnSuccessListener {
                Log.d("LogLeaveReq", "Leave Request successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("LogLeaveReq", "Database write failed", exception)
            }
    }




    data class LeaveRequest(val leaveType: String, val fromDate: String, val toDate: String, val document: String)

    private fun showDatePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, theYear, monthOfYear, dayOfMonth ->
            val formattedDate = "$theYear-${monthOfYear + 1}-$dayOfMonth"
            textView.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}