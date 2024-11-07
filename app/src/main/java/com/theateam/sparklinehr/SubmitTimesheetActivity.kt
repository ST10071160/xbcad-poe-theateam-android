package com.theateam.sparklinehr

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.theateam.sparklinehr.databinding.ActivitySubmitTimesheetBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SubmitTimesheetActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySubmitTimesheetBinding

    private val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private var selectedDate: String? = null
    private var selectedLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySubmitTimesheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_submit_timesheet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.backBtn.setOnClickListener {
            finish()
        }



        val prevArrow: ImageButton = findViewById(R.id.submitTimesheetPreviousWeekBtn)
        val nextArrow: ImageButton = findViewById(R.id.submitTimesheetNextWeekBtn)
        val currentWeekTextView: TextView = findViewById(R.id.submitTimesheetCurrentWeekTextView)

        // Display the current week when the activity starts
        displayCurrentWeek(currentWeekTextView)

        // Navigate to the previous week
        prevArrow.setOnClickListener {
            Log.d("DailyMealsActivity", "Previous button clicked")
            updateCalendar(-1)
            displayCurrentWeek(currentWeekTextView)
        }

        // Navigate to the next week
        nextArrow.setOnClickListener {
            Log.d("DailyMealsActivity", "Next button clicked")
            updateCalendar(1)
            displayCurrentWeek(currentWeekTextView)
        }

        // Set click listeners for each day's layout
        setDayClickListener(
            R.id.submitTimesheetSundayLayout,
            R.id.submitTimesheetSundayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetMondayLayout,
            R.id.submitTimesheetMondayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetTuesdayLayout,
            R.id.submitTimesheetTuesdayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetWednesdayLayout,
            R.id.submitTimesheetWednesdayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetThursdayLayout,
            R.id.submitTimesheetThursdayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetFridayLayout,
            R.id.submitTimesheetFridayNumberTextView
        )
        setDayClickListener(
            R.id.submitTimesheetSaturdayLayout,
            R.id.submitTimesheetSaturdayNumberTextView
        )

    }

    private fun updateCalendar(weeks: Int) {
        calendar.add(Calendar.WEEK_OF_YEAR, weeks)
    }

    private fun displayCurrentWeek(currentWeekTextView: TextView) {
        val startOfWeek = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        }
        val endOfWeek = (calendar.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            add(Calendar.DAY_OF_WEEK, 6)
        }
        val startDate = sdf.format(startOfWeek.time)
        val endDate = sdf.format(endOfWeek.time)
        currentWeekTextView.text = "Week of $startDate"

        updateDaysOfWeek(startOfWeek)
    }

    private fun updateDaysOfWeek(startOfWeek: Calendar) {
        val dayLabels = listOf(
            R.id.submitTimesheetSundayNumberTextView,
            R.id.submitTimesheetMondayNumberTextView,
            R.id.submitTimesheetTuesdayNumberTextView,
            R.id.submitTimesheetWednesdayNumberTextView,
            R.id.submitTimesheetThursdayNumberTextView,
            R.id.submitTimesheetFridayNumberTextView,
            R.id.submitTimesheetSaturdayNumberTextView
        )
        val dayNames = listOf(
            R.id.submitTimesheetSundayLbl,
            R.id.submitTimesheetMondayLbl,
            R.id.submitTimesheetTuesdayLbl,
            R.id.submitTimesheetWednesdayLbl,
            R.id.submitTimesheetThursdayLbl,
            R.id.submitTimesheetFridayLbl,
            R.id.submitTimesheetSaturdayLbl
        )

        dayLabels.zip(dayNames).forEachIndexed { i, (dayLabelId, dayNameId) ->
            val dayOfMonth = startOfWeek.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = SimpleDateFormat("E", Locale.getDefault()).format(startOfWeek.time)

            val dayNumber: TextView = findViewById(dayLabelId)
            val dayLabel: TextView = findViewById(dayNameId)

            dayNumber.text = dayOfMonth.toString()
            dayLabel.text = dayOfWeek

            startOfWeek.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun setDayClickListener(layoutId: Int, textViewId: Int) {
        val layout: LinearLayout = findViewById(layoutId)
        layout.setOnClickListener {
            // Highlight the selected layout
            highlightSelectedLayout(layout)

            // Get the selected day from the corresponding TextView
            val dayNumberTextView: TextView = findViewById(textViewId)
            val selectedDay = dayNumberTextView.text.toString().toInt()

            // Format the day to ensure two digits
            val formattedDay = String.format("%02d", selectedDay)

            // Get the currently displayed month and year
            val currentMonthYear = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(calendar.time)

            // Combine formatted day, month, and year
            selectedDate = "$formattedDay $currentMonthYear"

            Log.d("DailyMealsActivity", "Selected date: $selectedDate")
            saveSelectedDate(selectedDate!!)

            // Show a Toast with the full date (day, month, and year)
            Toast.makeText(this, "You have selected $selectedDate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightSelectedLayout(layout: LinearLayout) {
        // Reset the background color of the previously selected layout
        selectedLayout?.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))

        // Set the background color for the currently selected layout
        layout.setBackgroundColor(Color.parseColor("#37F269"))

        // Store the currently selected layout
        selectedLayout = layout
    }

    private fun saveSelectedDate(date: String) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedDate", date)
        editor.apply()
    }
}