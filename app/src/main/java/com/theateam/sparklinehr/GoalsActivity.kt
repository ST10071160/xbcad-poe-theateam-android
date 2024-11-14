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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private lateinit var goalsRecyclerView: RecyclerView
    private var goalNameList = ArrayList<String>()
    // Declare adapter as lateinit
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

        loadGoals()



        // Declare adapter variable
        adapter = GoalAdapter(goalNameList,
            onDeleteClick = { goal ->
                // Remove the goal from the list and notify the adapter
                goalNameList.remove(goal)
                adapter.notifyDataSetChanged()
            },
            onEditClick = { goal ->
                // Implement your edit functionality here
                val intent = Intent(this, AddOrUpdateActivity::class.java)
                intent.putExtra("editgoal", true)
                intent.putExtra("goalName", goal)
                startActivity(intent)
//                Toast.makeText(this, "Edit $goal", Toast.LENGTH_SHORT).show()
            }
        )
        binding.goalsRecyclerView.adapter = adapter


        binding.btnAddNewGoal.setOnClickListener() {
            val intent = Intent(this, AddOrUpdateActivity::class.java)
            startActivity(intent)
        }







    }



    private fun loadGoals() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Fetch all goals for the current employee
        dbRef.child("Goals").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    goalNameList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum)) {
                            // Get the payslip data for this key
                            val goal = childSnapshot.getValue(Goal::class.java)
                            if (goal != null) {
                                goalNameList.add(goal.goalName)
                                Log.d("UserInfo", "Payslip found: $goal")
                            } else {
                                Log.e("UserInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    adapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Payslips fetched successfully: $goalNameList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }




    data class Goal(val goalName: String, val dateAdded: String, val dateAchieveBy: String, val goalDesc: String)
}