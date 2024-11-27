package com.theateam.sparklinehr

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.theateam.sparklinehr.databinding.ActivityAddOrUpdateBinding

class UpdateContactDetailsActivity : AppCompatActivity() {

    private lateinit var updatedPhone: EditText
    private lateinit var updatedEmail: EditText
    private lateinit var updatedAddress: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_contact_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updatedPhone = findViewById(R.id.updateContactDetailsPersonalPhoneNumberEditText)
        updatedEmail = findViewById(R.id.updateContactDetailsEmailEditText)
        updatedAddress = findViewById(R.id.updateContactDetailsPersonalAddressEditText)
        updateButton = findViewById(R.id.updateContactDetailsSaveChangesBtn)
        backButton = findViewById(R.id.back_btn)


        updateButton.setOnClickListener() {
            val phoneNo = updatedPhone.text.toString()
            val email = updatedEmail.text.toString()
            val address = updatedAddress.text.toString()

            updateUserContactDetails(phoneNo, email, address)
        }

        backButton.setOnClickListener(){
            finish()
        }

    }



    //this will update the specific fields related to user contacts based on what has been entered by the user
    fun updateUserContactDetails(phoneNo: String, email: String, address: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Data to update
        val updateMap= mapOf(
            "Contact" to phoneNo,
            "Email" to email,
            "Address" to address
        )

        // Perform the update
        dbRef.child("employees_sparkline").child(userNum).child("employee")
            .updateChildren(updateMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Contact Details Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to Update Contact Details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}