package com.theateam.sparklinehr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityAddOrUpdateBinding

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

        val boolEdit = intent.getBooleanExtra("editgoal", false)

        if (boolEdit) {
            binding.aboutHeadingTextView.text = "Update Goal"
            binding.selectedGoalUpdateGoalBtn.text = "Update Goal"
        } else {
            binding.aboutHeadingTextView.text = "Add Goal"
            binding.selectedGoalUpdateGoalBtn.text = "Add Goal"
        }



    }
}