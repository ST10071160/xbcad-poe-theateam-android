package com.theateam.sparklinehr

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.GoalsActivity.Goal
import com.theateam.sparklinehr.databinding.ActivityViewTrainingDetailsBinding

class ViewSelectedGoalActivity : AppCompatActivity() {

    private lateinit var goal: Goal
    private lateinit var goalName: String

    private lateinit var goalNameT: TextView
    private lateinit var goalDateAdded: TextView
    private lateinit var goalDateAchieveBy: TextView
    private lateinit var goalDesc: TextView

    private lateinit var backButton:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_selected_goal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        goalNameT = findViewById(R.id.viewGoalGoalNameTextView)
        goalDateAdded = findViewById(R.id.viewGoalDateAddedTextView)
        goalDateAchieveBy = findViewById(R.id.viewGoalDateAchieveByTextView)
        goalDesc = findViewById(R.id.viewGoalGoalDescTextView)

        backButton = findViewById(R.id.back_btn)


        goalName = intent.getStringExtra("goalName").toString()
        if (goalName != null) {
            Log.d("ViewSelectedGoal", "Goal received: $goalName")
        } else {
            Log.e("ViewSelectedGoal", "Goal is null. Intent extras: ${intent?.extras}")
            finish()
        }

        loadGoal()


        backButton.setOnClickListener{
            finish()
        }
    }


    private fun loadGoal() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)

        val key = "${userNum},${goalName}"

        dbRef.child("Goals").child(key).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val snapshotKey = dataSnapshot.key
                    if (snapshotKey != null) {
                        // Get the payslip data for this key
                        goal = dataSnapshot.getValue(Goal::class.java)!!
                        if (goal != null) {

                            goalNameT.text = goal.goalName
                            goalDateAdded.text = goal.dateAdded
                            goalDateAchieveBy.text = goal.dateAchieveBy
                            goalDesc.text = goal.goalDesc



                            Log.d("GoalInfo", "Goal found: $goal")
                        } else {
                            Log.e("GoalInfo", "No data found for key: $key")
                        }
                    }

                    Log.d("FIREBASE_SUCCESS", "Payslips fetched successfully: $goal")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }

    data class Goal(val goalName: String = "",
                    val dateAdded: String = "",
                    val dateAchieveBy: String = "",
                    val goalDesc: String = "")
}