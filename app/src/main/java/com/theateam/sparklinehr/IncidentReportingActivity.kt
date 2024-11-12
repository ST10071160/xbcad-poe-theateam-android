package com.theateam.sparklinehr

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.theateam.sparklinehr.databinding.ActivityIncidentReportingBinding
import java.time.LocalDate
import kotlin.random.Random

class IncidentReportingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityIncidentReportingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityIncidentReportingBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_incident_reporting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }


        binding.incidentReportingSubmitIncidentBtn.setOnClickListener()
        {
            if(binding.incidentReportingRemainAnonCheckBox.isActivated)
            {
                val randomVal = Random.nextInt(10000, 100000)


                val passTitle = binding.incidentReportingIncidentTitleTextView.text.toString()
                val passDesc = binding.incidentReportingIssueTextView.text.toString()

                if (passTitle.isEmpty() || passDesc.isEmpty()) {
                    Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
                } else {
                    writeToFirebase(randomVal.toString(), passTitle, passDesc)

                    Toast.makeText(this, "Goal added", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                //val sharedPreferences = applicationContext.getSharedPreferences("EmailPref", Context.MODE_PRIVATE)
                //val userEmail = sharedPreferences.getString("USER_EMAIL", null)


                val userEmail = "futuregorilla99@gmail.com"
                val passTitle = binding.incidentReportingIncidentTitleTextView.text.toString()
                val passDesc = binding.incidentReportingIssueTextView.text.toString()

                if (passTitle.isEmpty() || passDesc.isEmpty()) {
                    Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
                } else {
                    writeToFirebase(userEmail, passTitle, passDesc)

                    Toast.makeText(this, "Goal added", Toast.LENGTH_LONG).show()
                }
            }
        }

    }


    private fun writeToFirebase(userEmail: String, passTitle: String, passDesc: String) {

        val email = userEmail?.replace(".", "").toString()

        val currDate = LocalDate.now()

        val entryID = email + "," + currDate

        val database = Firebase.database
        val dbRef = database.getReference("HRApp")
        val entry = Issue(userEmail, passTitle, passDesc)

        dbRef.child("IssueReports").child(entryID).setValue(entry)
            .addOnSuccessListener {
                Log.d("LogIssues", "Issue successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("LogIssues", "Database write failed", exception)
            }
    }

    data class Issue(val userEmail: String, val passTitle: String, val passDesc: String)
}