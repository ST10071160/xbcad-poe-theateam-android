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
import com.theateam.sparklinehr.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val apiService: ApiService = ApiClient.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_change_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnChangePassword.setOnClickListener {
            //val employeeId = intent.getStringExtra("EMPLOYEE_ID") // Pass from LoginActivity
            val newPassword = binding.newPasswordEditText.text.toString()
//            val employeeId = intent.getStringExtra("EMPLOYEE_ID")

            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val employeeId = sharedPreferences.getString("EMPLOYEE_ID", null)

            if (employeeId != null) {
                Log.d("employeeId", employeeId)
                changePassword(employeeId ?: "", newPassword)
            } else {
                Log.d("employeeId", "Employee ID is null")
            }
        }
    }


    private fun changePassword(employeeId: String, newPassword: String) {
        val changePasswordRequest = PasswordChangeRequest(employeeId, newPassword)

        apiService.changePassword(changePasswordRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ChangePasswordActivity, LoginActivity::class.java))
                } else {
                    Toast.makeText(this@ChangePasswordActivity, "Failed to change password.", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Error: ${response.code()}, ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@ChangePasswordActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_FAILURE", "Failed to make request: ${t.message}")
            }
        })
    }
}