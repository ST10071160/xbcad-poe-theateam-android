package com.theateam.sparklinehr

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivitySelfServiceBinding

class SelfServiceActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySelfServiceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelfServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_self_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.selfServiceViewPersonalInfoBtn.setOnClickListener{
            val intentPersonalInfo = Intent(this@SelfServiceActivity, ViewPersonalInformationActivity::class.java)
            startActivity(intentPersonalInfo)

        }

        binding.selfServiceUpdatePersonalInfoBtn.setOnClickListener{
            val intentUpdatePersonalInfo = Intent(this@SelfServiceActivity, UpdatePersonalInformationActivity::class.java)
            startActivity(intentUpdatePersonalInfo)
        }

        binding.selfServiceUpdatePaymentInfoBtn.setOnClickListener(){
            val intentUpdatePaymentInfo = Intent(this@SelfServiceActivity, UpdatePaymentInformationActivity::class.java)
            startActivity(intentUpdatePaymentInfo)

        }

        binding.selfServiceViewPayslipInfoBtn.setOnClickListener(){
            val intentViewPaymentInfo = Intent(this@SelfServiceActivity, ViewPayslipHistoryActivity::class.java)
            startActivity(intentViewPaymentInfo)
        }



    }
}