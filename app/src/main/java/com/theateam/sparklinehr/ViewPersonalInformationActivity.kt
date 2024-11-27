package com.theateam.sparklinehr

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityViewPersonalInformationBinding

class ViewPersonalInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPersonalInformationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewPersonalInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_view_personal_information)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        getEmployeeInfo()
        getJobTitle()
    }

    //this will retrieve all employee info in order to display the employee's name, email, and phone number back to them
    private fun getEmployeeInfo() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        dbRef.child("employees_sparkline").child(userNum).child("employee")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        binding.viewPersonalInfoFirstNameTextView.setText("${user.Name}")
                        binding.viewPersonalInfoWorkEmailTextView.setText("${user.Email}")
                        binding.viewPersonalInfoPhoneNumTextView.setText("${user.Contact}")


                        Log.d("UserInfo", "User info: $user")
                    } else {
                        Log.e("UserInfo", "No data found for key: $userNum")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Failed to get data: ${error.message}")
                }
            })
    }


    //this method will get only the job title of the current user, due to the way that the storage is formatted, a separate method was required for this
    private fun getJobTitle() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        dbRef.child("employees_sparkline").child(userNum).child("jobdetails")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val job = dataSnapshot.getValue(JobDetails::class.java)
                    if (job != null) {

                        binding.viewPersonalInfoJobTitleTextView.setText("${job.JobTitle}")


                        Log.d("JobInfo", "Job info: $job")
                    } else {
                        Log.e("JobInfo", "No data found for key: $userNum")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("JobInfo", "Failed to get data: ${error.message}")
                }
            })
    }

    data class UserInfo(
        var Address: String? = null,
        var Contact: String? = null,
        var DateOfBirth: String? = null,
        var Email: String? = null,
        var EmergencyContactName: String? = null,
        var EmergencyContactNumber: String? = null,
        var EmergencyContactRelationship: String? = null,
        var Name: String? = null
    )


    data class JobDetails(
        var Department: String? = null,
        var EmployeeId: String? = null,
        var EmploymentType: String? = null,
        var HireDate: String? = null,
        var JobDescription: String? = null,
        var JobTitle: String? = null,
        var Manager: String? = null
    )

}