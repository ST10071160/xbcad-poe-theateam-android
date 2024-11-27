package com.theateam.sparklinehr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.theateam.sparklinehr.GoalsActivity.Goal
import com.theateam.sparklinehr.databinding.ActivityGoalsBinding
import com.theateam.sparklinehr.databinding.ActivityPerformanceBinding
import com.theateam.sparklinehr.databinding.ActivityTrainingBinding
import java.io.Serializable

class PerformanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerformanceBinding
    private lateinit var performanceRecyclerView: RecyclerView
    private var reviewList = ArrayList<PerformanceReview>()


    lateinit var adapter: PerformanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPerformanceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //setContentView(R.layout.activity_performance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener() {
            finish()
        }



        performanceRecyclerView = findViewById(R.id.performanceRecyclerView)
        performanceRecyclerView.layoutManager = LinearLayoutManager(this)

        loadReviews()

        // Declare adapter variable
        adapter = PerformanceAdapter(reviewList) { review ->
            if (review != null) {
                val intent = Intent(this, ViewPerformanceDetailsActivity::class.java)
                intent.putExtra("review", review)
                Log.d("PerformanceIntent", "Passing Review: $review")
                startActivity(intent)
            } else {
                Log.e("PerformanceIntent", "Performance Review is null!")
            }
        }
        performanceRecyclerView.adapter = adapter
    }



    //this will load a list of performance reviews that are relevant to the logged in user and display in a list
    private fun loadReviews() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)

        // Fetch all goals for the current employee
        dbRef.child("performanceReviews").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    reviewList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum!!)) {
                            // Get the performance review data for this current user
                            val review = childSnapshot.getValue(PerformanceReview::class.java)
                            if (review != null) {
                                reviewList.add(review)
                                Log.d("PerformanceInfo", "Review found: $review")
                            } else {
                                Log.e("PerformanceInfo", "No data found for key: $key")
                            }
                        }
                    }

                    adapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Reviews fetched successfully: $reviewList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }




    data class PerformanceReview(val EmployeeNumber: String = "",
                    val ReviewDate: String = "",
                    val PerformanceRating: String = "",
                    val Feedback: String = ""): Serializable


}
