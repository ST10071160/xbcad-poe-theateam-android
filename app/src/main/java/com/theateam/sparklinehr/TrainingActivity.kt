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
import com.theateam.sparklinehr.databinding.ActivityTrainingBinding
import java.io.Serializable

class TrainingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainingBinding
    private lateinit var trainingRecyclerView: RecyclerView
    private var trainingList = ArrayList<TrainingInfo>()


    lateinit var adapter: TrainingAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_training)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener() {
            finish()
        }



        trainingRecyclerView = findViewById(R.id.trainingRecyclerView)
        trainingRecyclerView.layoutManager = LinearLayoutManager(this)

        loadTraining()

        // Declare adapter variable
        adapter = TrainingAdapter(trainingList) { training ->
            if (training != null) {
                val intent = Intent(this, ViewTrainingDetailsActivity::class.java)
                intent.putExtra("training", training)
                Log.d("TrainingIntent", "Passing Training: $training")
                startActivity(intent)
            } else {
                Log.e("TrainingIntent", "Training is null!")
            }
        }
        trainingRecyclerView.adapter = adapter
    }




    //this method will load all of the training objects that are related to the current user from the firebase
    private fun loadTraining() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null)

        // Fetch all goals for the current employee
        dbRef.child("trainings").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    trainingList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum!!)) {
                            // Get the training data for this key
                            val training = childSnapshot.getValue(TrainingInfo::class.java)
                            if (training != null) {
                                trainingList.add(training)
                                Log.d("TrainingInfo", "Training found: $training")
                            } else {
                                Log.e("TrainingInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    adapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Trainings fetched successfully: $trainingList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }




    data class TrainingInfo(val EmployeeNumber: String = "",
                                 val CourseName: String = "",
                                 val CourseLink: String = "",
                                 val CompletionDate: String = ""): Serializable
}