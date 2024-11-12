package com.theateam.sparklinehr

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.theateam.sparklinehr.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
//    private lateinit var goalsRecyclerView: RecyclerView
    private val goals = mutableListOf("Goal 1", "Goal 2", "Goal 3") // Sample data
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



        // Declare adapter variable
        adapter = GoalAdapter(goals,
            onDeleteClick = { goal ->
                // Remove the goal from the list and notify the adapter
                goals.remove(goal)
                adapter.notifyDataSetChanged()
            },
            onEditClick = { goal ->
                // Implement your edit functionality here
                val intent = Intent(this, AddOrUpdateActivity::class.java)
                intent.putExtra("editgoal", true)
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
}