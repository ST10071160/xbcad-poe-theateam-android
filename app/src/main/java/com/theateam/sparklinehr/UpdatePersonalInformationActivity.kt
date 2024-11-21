package com.theateam.sparklinehr

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityUpdatePersonalInformationBinding

class UpdatePersonalInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePersonalInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUpdatePersonalInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_update_personal_information)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.updatePersonalInfoUpdateContactBtn.setOnClickListener{
            val intent = Intent(this, UpdateContactDetailsActivity::class.java)
            startActivity(intent)
        }

        binding.updatePersonalInfoUpdateEmergencyContactBtn.setOnClickListener{
            val intent = Intent(this, UpdateEmergencyContactDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}