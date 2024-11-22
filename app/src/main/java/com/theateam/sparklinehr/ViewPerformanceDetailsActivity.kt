package com.theateam.sparklinehr

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityViewPerformanceDetailsBinding
import com.theateam.sparklinehr.databinding.ActivityViewSelectedPayslipBinding

class ViewPerformanceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPerformanceDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewPerformanceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setContentView(R.layout.activity_view_performance_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }


        val review = intent.getSerializableExtra("review") as PerformanceActivity.PerformanceReview
        if (review != null) {
            Log.d("ViewSelectedReview", "Review received: $review")
        } else {
            Log.e("ViewSelectedReview", "Review is null. Intent extras: ${intent?.extras}")
            finish()
        }


        binding.viewPerformanceDetailsEmpNumTextView.text = review.EmployeeNumber
        val formattedDate = review.ReviewDate.split("T")[0]
        binding.viewPerformanceDetailsReviewDateTextView.text = formattedDate
        binding.viewPerformanceDetailsRatingTextView.text = review.PerformanceRating
        binding.viewPerformanceDetailsFeedbackTextView.text = review.Feedback

    }
}