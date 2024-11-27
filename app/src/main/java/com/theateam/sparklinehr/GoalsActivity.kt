package com.theateam.sparklinehr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var goalsRecyclerView: RecyclerView
    private var goalList = ArrayList<String>()


    lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_goals)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener() {
            finish()
        }


        goalsRecyclerView = findViewById(R.id.goalsRecyclerView)
        goalsRecyclerView.layoutManager = LinearLayoutManager(this)

        loadGoals()

        // Declare adapter variable
        adapter = GoalAdapter(goalList,
            onDeleteClick = { goal ->
                removeGoal(goal)
                goalList.remove(goal)
                adapter.notifyDataSetChanged()
            },
            onEditClick = { goal ->
                // Implement your edit functionality here
                val intent = Intent(this, AddOrUpdateActivity::class.java)
                intent.putExtra("editgoal", true)
                intent.putExtra("goalName", goal)
                startActivity(intent)
            },
            onGoalClick = { goal ->
                val intent = Intent(this, ViewSelectedGoalActivity::class.java)
                intent.putExtra("goalName", goal)
                startActivity(intent)
            }
        )
        goalsRecyclerView.adapter = adapter


        binding.btnAddNewGoal.setOnClickListener() {
            val intent = Intent(this, AddOrUpdateActivity::class.java)
            startActivity(intent)
        }

    }

    //this method will remove the selected goal from the database, and remove the goal from the list displayed
    private fun removeGoal(goal: String) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)

        val key = "${userNum},${goal}"

        dbRef.child("Goals").child(key).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Data successfully removed for key: $key")
                Toast.makeText(this, "Goal successfully removed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Failed to remove data: ${exception.message}", exception)
                Toast.makeText(this, "Failed to remove goal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    //this will load all goals that are linked to the currently logged in user, and display them in a recycler view as a list
    private fun loadGoals() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)

        // Fetch all goals for the current employee
        dbRef.child("Goals").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    goalList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum!!)) {
                            // Get the payslip data for this key
                            val goal = childSnapshot.getValue(Goal::class.java)
                            if (goal != null) {
                                goalList.add(goal.goalName)
                                Log.d("GoalInfo", "Goal found: $goal")
                            } else {
                                Log.e("GoalInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    adapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Payslips fetched successfully: $goalList")
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