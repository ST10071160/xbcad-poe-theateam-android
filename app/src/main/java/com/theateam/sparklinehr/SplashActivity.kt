package com.theateam.sparklinehr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sparkleAnimationView = findViewById<LottieAnimationView>(R.id.sparkleAnimation)

        // Delay to start sparkle animation (e.g., 500 ms after image appears)
        Handler(Looper.getMainLooper()).postDelayed({
            sparkleAnimationView.playAnimation()
        }, 2000) // Start sparkle after 0.5 seconds

        // Delay to move to MainActivity after total 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3500) // Total splash time: 3 seconds
    }
}