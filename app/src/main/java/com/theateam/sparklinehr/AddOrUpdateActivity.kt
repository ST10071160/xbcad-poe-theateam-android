package com.theateam.sparklinehr

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.theateam.sparklinehr.databinding.ActivityAddOrUpdateBinding
import java.util.Calendar

class AddOrUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddOrUpdateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddOrUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_add_or_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener() {
            finish()
        }

        binding.selectedGoalDateAddedTextView.setOnClickListener() {
            showDatePicker(binding.selectedGoalDateAddedTextView)
        }

        binding.selectedGoalDateAchieveByTextView.setOnClickListener() {
            showDatePicker(binding.selectedGoalDateAchieveByTextView)
        }

        val boolEdit = intent.getBooleanExtra("editgoal", false)

        if (boolEdit) {
            binding.aboutHeadingTextView.text = "Update Goal"
            binding.selectedGoalUpdateGoalBtn.text = "Update Goal"


            binding.selectedGoalUpdateGoalBtn.setOnClickListener()
            {
                val passName = binding.selectedGoalGoalNameTextView.text.toString()
                val passAdded = binding.selectedGoalDateAddedTextView.text.toString()
                val passAchieve = binding.selectedGoalDateAchieveByTextView.text.toString()
                val passDesc = binding.selectedGoalGoalDescriptionTextView.text.toString()

                if (passName.isEmpty() || passAdded.isEmpty() || passAchieve.isEmpty() || passDesc.isEmpty()) {
                    Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
                } else {
                    updateGoals(passName, passAdded, passAchieve, passDesc)

                    Toast.makeText(this, "Goal added", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            binding.aboutHeadingTextView.text = "Add Goal"
            binding.selectedGoalUpdateGoalBtn.text = "Add Goal"


            binding.selectedGoalUpdateGoalBtn.setOnClickListener()
            {
                val passName = binding.selectedGoalGoalNameTextView.text.toString()
                val passAdded = binding.selectedGoalDateAddedTextView.text.toString()
                val passAchieve = binding.selectedGoalDateAchieveByTextView.text.toString()
                val passDesc = binding.selectedGoalGoalDescriptionTextView.text.toString()

                if (passName.isEmpty() || passAdded.isEmpty() || passAchieve.isEmpty() || passDesc.isEmpty()) {
                    Toast.makeText(this, "Some fields are missing", Toast.LENGTH_SHORT).show()
                } else {
                    writeToFirebase(passName, passAdded, passAchieve, passDesc)

                    Toast.makeText(this, "Goal added", Toast.LENGTH_LONG).show()
                }
            }
        }



    }



    private fun updateGoals(goalName: String, dateAdded: String, dateAchieveBy: String, goalDesc: String) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val key = "${userNum},${dateAdded}"


        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparklineHR")

        val updateMap = mapOf<String, Any>(
            "goalName" to goalName,
            "dateAdded" to dateAdded,
            "dateAchieveBy" to dateAchieveBy,
            "goalDesc" to goalDesc
        )

        dbRef.child("Goals").child(key).updateChildren(updateMap)
            .addOnSuccessListener {
                Log.d("UpdateGoals", "Employee Goal successfully updated!")
                Toast.makeText(this, "Goal Update Successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Goal Update Failed", Toast.LENGTH_SHORT).show()
                Log.e("UpdateGoals", "Failed to update Timesheet", exception)
            }
    }



    private fun writeToFirebase(goalName: String, dateAdded: String, dateAchieveBy: String, goalDesc: String) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val key = "${userNum},${dateAdded}"

        val database = Firebase.database
        val dbRef = database.getReference("SparklineHR")
        val entry = Goal(goalName, dateAdded, dateAchieveBy, goalDesc)

        dbRef.child("Goals").child(key).setValue(entry)
            .addOnSuccessListener {
                Log.d("LogGoals", "Goal successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("LogGoals", "Database write failed", exception)
            }
    }


    data class Goal(val goalName: String, val dateAdded: String, val dateAchieveBy: String, val goalDesc: String)


    private fun showDatePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, theYear, monthOfYear, dayOfMonth ->
            val formattedDate = "$dayOfMonth-${monthOfYear + 1}-$theYear"
            textView.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}