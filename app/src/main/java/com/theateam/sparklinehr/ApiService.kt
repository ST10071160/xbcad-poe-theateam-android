package com.theateam.sparklinehr

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class LoginRequest(val employeeId: String, val password: String)
data class PasswordChangeRequest(val employeeId: String, val newPassword: String)
data class ApiResponse(val message: String, val error: String? = null)

interface ApiService {

    // Endpoint for OTP login (first-time login with OTP)
    @POST("loginotp")
    fun loginWithOtp(@Body request: LoginRequest): Call<ApiResponse>

    // Endpoint for subsequent login with new password
    @POST("login")
    fun loginWithPassword(@Body request: LoginRequest): Call<ApiResponse>

    // Endpoint for changing password after OTP login
    @POST("change-password")
    fun changePassword(@Body request: PasswordChangeRequest): Call<ApiResponse>
}
