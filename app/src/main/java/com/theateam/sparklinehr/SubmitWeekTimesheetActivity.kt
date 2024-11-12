package com.theateam.sparklinehr

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SubmitWeekTimesheetActivity : AppCompatActivity() {

    private lateinit var weekDatesTextView: TextView
    private lateinit var submitButton: Button
    private lateinit var dayDateTextViews: List<TextView>
    private lateinit var hoursSpinners: List<Spinner>
    private lateinit var previousWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton
    private lateinit var backbtn: ImageButton

    private val currentWeek: Calendar = Calendar.getInstance()
    private var displayedWeek: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_submit_week_timesheet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize views
        weekDatesTextView = findViewById(R.id.weekDatesTextView)
        submitButton = findViewById(R.id.submitButton)
        backbtn = findViewById(R.id.back_btn)

        previousWeekButton = findViewById(R.id.previousWeekButton)
        nextWeekButton = findViewById(R.id.nextWeekButton)

        backbtn.setOnClickListener {
            finish()
        }

        dayDateTextViews = listOf(
            findViewById(R.id.mondayDateTextView),
            findViewById(R.id.tuesdayDateTextView),
            findViewById(R.id.wednesdayDateTextView),
            findViewById(R.id.thursdayDateTextView),
            findViewById(R.id.fridayDateTextView)
        )

        hoursSpinners = listOf(
            findViewById(R.id.mondaySpinner),
            findViewById(R.id.tuesdaySpinner),
            findViewById(R.id.wednesdaySpinner),
            findViewById(R.id.thursdaySpinner),
            findViewById(R.id.fridaySpinner)
        )

        setupWeekNavigation()
        setupSpinners()
        updateWeekDates(displayedWeek)
    }

    private fun setupWeekNavigation() {
        previousWeekButton.setOnClickListener {
            changeWeek(-1)
        }
        nextWeekButton.setOnClickListener {
            changeWeek(1)
        }
        updateWeekNavigationButtons()
    }

    private fun changeWeek(offset: Int) {
        displayedWeek.add(Calendar.WEEK_OF_YEAR, offset)
        updateWeekDates(displayedWeek)
        updateWeekNavigationButtons()
    }

    private fun updateWeekDates(calendar: Calendar) {
        val dayFormat = SimpleDateFormat("EEEE dd/MM", Locale.getDefault())
        val weekStart = calendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val weekEnd = calendar.clone() as Calendar
        weekEnd.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

        weekDatesTextView.text = "${dayFormat.format(weekStart.time)} - ${dayFormat.format(weekEnd.time)}"

        // Update each day’s date and disable future dates
        for (i in 0..4) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_WEEK, i)
            dayDateTextViews[i].text = dayFormat.format(day.time)

            // Disable spinner if the date is in the future
            hoursSpinners[i].isEnabled = !day.after(Calendar.getInstance())
        }
    }

    private fun updateWeekNavigationButtons() {
        // Set displayedWeek's time to the start of the week for accurate comparison
        val startOfDisplayedWeek = displayedWeek.clone() as Calendar
        startOfDisplayedWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val startOfCurrentWeek = currentWeek.clone() as Calendar
        startOfCurrentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Disable the "Next" button if the displayed week is the current or future week
        nextWeekButton.isEnabled = startOfDisplayedWeek.before(startOfCurrentWeek)

        // Keep the "Previous" button always enabled to allow indefinite backward navigation
        previousWeekButton.isEnabled = true
    }

    private fun setupSpinners() {
        val hoursAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.hours_array,
            android.R.layout.simple_spinner_item
        )
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        hoursSpinners.forEach { spinner ->
            spinner.adapter = hoursAdapter
        }
    }
}