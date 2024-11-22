package com.theateam.sparklinehr

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityViewPerformanceDetailsBinding
import com.theateam.sparklinehr.databinding.ActivityViewTrainingDetailsBinding

class ViewTrainingDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewTrainingDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewTrainingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_view_training_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }



        val training = intent.getSerializableExtra("training") as TrainingActivity.TrainingInfo
        if (training != null) {
            Log.d("ViewSelectedTraining", "Training received: $training")
        } else {
            Log.e("ViewSelectedTraining", "Training is null. Intent extras: ${intent?.extras}")
            finish()
        }


        binding.viewTrainingDetailsEmpNumTextView.text = training.EmployeeNumber
        binding.viewTrainingDetailsCourseNameTextView.text = training.CourseName
        binding.viewTrainingDetailsCourseLinkTextView.text = training.CourseLink
        binding.viewTrainingDetailsCompletionDateTextView.text = training.CompletionDate
    }
}