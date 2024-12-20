package com.theateam.sparklinehr

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityDevelopmentAndGoalsMenuBinding

class DevelopmentAndGoalsMenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDevelopmentAndGoalsMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDevelopmentAndGoalsMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_development_and_goals_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.developmentAndGoalsGoalsCardLinearLayout.setOnClickListener{
            val intent = Intent(this, GoalsActivity::class.java)
            startActivity(intent)
        }

        binding.developmentAndGoalsTrainingCardLinearLayout.setOnClickListener{
            val intent = Intent(this, TrainingActivity::class.java)
            startActivity(intent)
        }

        binding.developmentAndGoalsPerformanceCardLinearLayout.setOnClickListener{
            val intent = Intent(this, PerformanceActivity::class.java)
            startActivity(intent)
        }

    }
}