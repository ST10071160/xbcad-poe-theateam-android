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
    }

    private fun getEmployeeInfo() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        dbRef.child(userNum).child("Personal Details")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(UserInfo::class.java)
                    if (user != null) {

                        binding.viewPersonalInfoFirstNameTextView.setText("${user.fullName}")
                        binding.viewPersonalInfoJobTitleTextView.setText("${user.jobTitle}")
                        binding.viewPersonalInfoWorkEmailTextView.setText("${user.workEmail}")
                        binding.viewPersonalInfoPhoneNumTextView.setText("${user.phoneNumber}")


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


    data class UserInfo(
        var fullName: String? = null,
        var jobTitle: String? = null,
        var phoneNumber: String? = null,
        var workEmail: String? = null
    )

}