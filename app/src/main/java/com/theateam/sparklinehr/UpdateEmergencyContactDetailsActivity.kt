package com.theateam.sparklinehr

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class UpdateEmergencyContactDetailsActivity : AppCompatActivity() {
    private lateinit var updatedName: EditText
    private lateinit var updatedRelationship: Spinner
    private lateinit var updatedPhone: EditText
    private lateinit var updateButton: Button
    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_emergency_contact_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updatedName = findViewById(R.id.updatePersonalInfoEmergencyNameEditText)

         lateinit var selectedItem: String

        updatedRelationship = findViewById(R.id.updatePersonalInfoRelationshipSpinner)

        val spinnerData = listOf("Spouse/Partner", "Parent", "Sibling", "Child", "Other")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        updatedRelationship.adapter = adapter

        updatedRelationship.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        updatedPhone = findViewById(R.id.updatePersonalInfoEmergencyNumberEditText)
        updateButton = findViewById(R.id.updatePersonalInfoSaveChangesBtn)
        backButton = findViewById(R.id.back_btn)


        updateButton.setOnClickListener() {
            val contactName = updatedName.text.toString()
            val contactRelationship = selectedItem
            val contactPhone = updatedPhone.text.toString()

            updateEmergencyContactDetails(contactName, contactRelationship, contactPhone)
        }

        backButton.setOnClickListener(){
            finish()
        }
    }



    fun updateEmergencyContactDetails(contactName: String, contactRelationship: String, contactPhone: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Data to update
        val updateMap= mapOf(
            "EmergencyContactName" to contactName,
            "EmergencyContactRelationship" to contactRelationship,
            "EmergencyContactNumber" to contactPhone
        )

        // Perform the update
        dbRef.child("employees_sparkline").child(userNum).child("employee")
            .updateChildren(updateMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Emergency Contact Details Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to Update Emergency Contact Details", Toast.LENGTH_SHORT).show()
                }
            }
    }
}