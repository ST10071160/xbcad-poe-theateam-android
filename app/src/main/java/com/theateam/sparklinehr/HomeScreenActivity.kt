package com.theateam.sparklinehr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    // Object for Navigation view
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_home_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_drawer, R.string.close_drawer)
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

//        val headerView = binding.navView.getHeaderView(0)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        binding.navView.setNavigationItemSelectedListener {
////            val intentProfile = Intent(this@MainActivity, ProfileSettingActivity::class.java)
//            //intentHome.putExtra("userEmail", email)
////            val intentSettings = Intent(this@MainActivity, SettingsActivity::class.java)
//
//
//            //starts intents for taking the user to the home and support screens, and will allow the user
//            when (it.itemId) {
////                R.id.navSettings -> startActivity(intentSettings)
////                R.id.navProfile -> startActivity(intentProfile)
//                R.id.navLogout -> LogoutAlertDialog()
//            }
//            true
//        }


        // Name in Action Bar
//        supportActionBar?.setTitle("Welcome to SparkLine HR")


        binding.homeScreenSelfServiceBtn.setOnClickListener{
            val intentSelfService = Intent(this@HomeScreenActivity, SelfServiceActivity::class.java)
            startActivity(intentSelfService)
        }

        binding.homeScreenSubmitTimesheetBtn.setOnClickListener{
            val intentSubmitTimesheet = Intent(this@HomeScreenActivity, SubmitWeekTimesheetActivity::class.java)
            startActivity(intentSubmitTimesheet)
        }

        binding.homeScreenDevelopmentGoalsBtn.setOnClickListener{
            val intentDevelopmentGoals = Intent(this@HomeScreenActivity, DevelopmentAndGoalsMenuActivity::class.java)
            startActivity(intentDevelopmentGoals)
        }

        binding.homeScreenLeaveRequestBtn.setOnClickListener{
            val intentSubmitTimesheet = Intent(this@HomeScreenActivity, LeaveRequestActivity::class.java)
            startActivity(intentSubmitTimesheet)
        }

        binding.homeScreenIncidentFeedbackBtn.setOnClickListener{
            val intentIncidentReporting = Intent(this@HomeScreenActivity, IncidentReportingActivity::class.java)
            startActivity(intentIncidentReporting)
        }



    }

    // Logout Dialog:
    // Author: DreamDevelopers
    // Link: https://www.youtube.com/watch?v=sFfP5qZojHQ&ab_channel=DreamDevelopers

    //this method will show a dialog to the user that will ask them whether they would like to log out or not
    private fun LogoutAlertDialog() {
        val alertdialog: AlertDialog = AlertDialog.Builder(this).create()
        alertdialog.setTitle("Logout")
        alertdialog.setMessage("Are you sure you want to Logout?")

        alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes"){
                dialog, which ->
            val intent = Intent(this@HomeScreenActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
        }

        alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No"){
                dialog, which ->
            dialog.dismiss()
        }
        alertdialog.show()

    }


    // this method must be overridden so that the navigation drawer can be opened
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Are you sure you want to Logout?")

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { dialog, which ->
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("EMPLOYEE_ID", "")
            editor.apply()

            super.onBackPressed()
            dialog.dismiss()
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}