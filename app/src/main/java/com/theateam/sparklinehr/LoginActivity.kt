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
import com.theateam.sparklinehr.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val apiService: ApiService = ApiClient.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginPageLoginBtn.setOnClickListener {
            // Handle login button click
//            val intent = Intent(this, HomeScreenActivity::class.java)
//            startActivity(intent)
            val employeeId = binding.loginScreenEditEmpNumPlainText.text.toString()
            val password = binding.loginScreenEditPasswordPlainText.text.toString()
            login(employeeId, password)
        }



    }


    private fun login(employeeId: String, password: String) {
        val loginRequest = LoginRequest(employeeId, password)

        apiService.loginWithOtp(loginRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.message == "OTP valid, please change your password.") {
                    // OTP login successful, redirect to ChangePasswordActivity
//                    intent.putExtra("EMPLOYEE_ID", employeeId)
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("EMPLOYEE_ID", employeeId)
                    editor.apply()
                    startActivity(Intent(this@LoginActivity, ChangePasswordActivity::class.java))

                } else {
                    // Attempt subsequent login
                    apiService.loginWithPassword(loginRequest).enqueue(object :
                        Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful) {
                                // Successful login, redirect to WelcomeActivity
                                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("EMPLOYEE_ID", employeeId)
                                editor.apply()
                                startActivity(Intent(this@LoginActivity, HomeScreenActivity::class.java))
                            } else {
                                Toast.makeText(this@LoginActivity, "Invalid password.", Toast.LENGTH_SHORT).show()
                                Log.e("API_ERROR", "Error: ${response.code()}, ${response.errorBody()?.string()}")
                            }
                        }
                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "Login failed.", Toast.LENGTH_SHORT).show()
                            Log.e("API_FAILURE", "Failed to make request: ${t.message}")
                        }
                    })
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_FAILURE", "Failed to make request: ${t.message}")
            }
        })
    }

}