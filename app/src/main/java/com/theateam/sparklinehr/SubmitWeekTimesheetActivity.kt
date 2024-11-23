package com.theateam.sparklinehr

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SubmitWeekTimesheetActivity : AppCompatActivity() {

    private lateinit var weekDatesTextView: TextView
    private lateinit var submitButton: Button
    private lateinit var dayDateTextViews: List<TextView>
    private lateinit var mondayHours: EditText
    private lateinit var tuesdayHours: EditText
    private lateinit var wednesdayHours: EditText
    private lateinit var thursdayHours: EditText
    private lateinit var fridayHours: EditText
    private lateinit var hoursInput: List<EditText>

    private val leavePeriods = mutableListOf<Pair<Calendar, Calendar>>()

    private lateinit var previousWeekButton: ImageButton
    private lateinit var nextWeekButton: ImageButton
    private lateinit var backbtn: ImageButton

    private var timesheet: Timesheet = Timesheet(0.0, 0.0, 0.0, 0.0, 0.0)

    private val currentWeek: Calendar = Calendar.getInstance()
    private var displayedWeek: Calendar = Calendar.getInstance()




    interface TimesheetCheckCallback {
        fun onResult(timesheetExists: Boolean, timesheet: Timesheet?)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_submit_week_timesheet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mondayHours = findViewById(R.id.mondayHoursTextview)
        tuesdayHours = findViewById(R.id.tuesdayHoursTextview)
        wednesdayHours = findViewById(R.id.wednesdayHoursTextview)
        thursdayHours = findViewById(R.id.thursdayHoursTextview)
        fridayHours = findViewById(R.id.fridayHoursTextview)


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

        hoursInput = listOf(
            findViewById(R.id.mondayHoursTextview),
            findViewById(R.id.tuesdayHoursTextview),
            findViewById(R.id.wednesdayHoursTextview),
            findViewById(R.id.thursdayHoursTextview),
            findViewById(R.id.fridayHoursTextview)
        )

        setupWeekNavigation()
        //setupSpinners()
        loadLeavePeriods()
        updateWeekDates(displayedWeek)


        submitButton.setOnClickListener {
            var monHours = 0.0
            var tueHours = 0.0
            var wedHours = 0.0
            var thuHours = 0.0
            var friHours = 0.0


            if(mondayHours.isVisible == true)
            {
                monHours = mondayHours.text.toString().toDouble()
            }

            if(tuesdayHours.isVisible == true)
            {
                tueHours = tuesdayHours.text.toString().toDouble()
            }

            if(wednesdayHours.isVisible == true)
            {
                wedHours = wednesdayHours.text.toString().toDouble()
            }

            if(thursdayHours.isVisible == true)
            {
                thuHours = thursdayHours.text.toString().toDouble()
            }

            if(fridayHours.isVisible == true)
            {
                friHours = fridayHours.text.toString().toDouble()
            }


            val weekKey = displayedWeek.clone() as Calendar
            weekKey.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            checkExistingTimesheet(weekKey, object : TimesheetCheckCallback {
                override fun onResult(timesheetExists: Boolean, existingTimesheet: Timesheet?) {
                    if (timesheetExists && existingTimesheet != null) {
                        updateTimesheet(weekKey, monHours, tueHours, wedHours, thuHours, friHours)
                    } else {
                        writeToFirebase(weekKey, monHours, tueHours, wedHours, thuHours, friHours)
                    }


                }
            })
        }
    }


    private fun checkExistingTimesheet(datePeriod: Calendar, callback: TimesheetCheckCallback) {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val timesheetKey = getTimesheetKey(userNum, datePeriod)

        dbRef.child("Timesheet entry").child(timesheetKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        timesheet = dataSnapshot.getValue(Timesheet::class.java)!!
                        Log.d("FIREBASE_SUCCESS", "Timesheet found: $timesheet")
                        callback.onResult(true, timesheet)
                    } else {
                        Log.e("FIREBASE_ERROR", "No data found for key: $timesheetKey")
                        callback.onResult(false, null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
                    callback.onResult(false, null)
                }
            })
    }

    private fun updateTimesheet(datePeriod: Calendar, monHours: Double, tueHours: Double, wedHours: Double, thuHours: Double, friHours: Double) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val timesheetKey = getTimesheetKey(userNum, datePeriod)

        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")

        val updatedMon = monHours + timesheet.monHours
        val updatedTue = tueHours + timesheet.tueHours
        val updatedWed = wedHours + timesheet.wedHours
        val updatedThu = thuHours + timesheet.thuHours
        val updatedFri = friHours + timesheet.friHours

        val totalHours = updatedMon + updatedTue + updatedWed + updatedThu + updatedFri

        if (totalHours < 40) {
            val updateMap = mapOf<String, Any>(
                "monHours" to updatedMon,
                "tueHours" to updatedTue,
                "wedHours" to updatedWed,
                "thuHours" to updatedThu,
                "friHours" to updatedFri
            )

            dbRef.child("Timesheet entry").child(timesheetKey).updateChildren(updateMap)
                .addOnSuccessListener {
                    Log.d("UpdateTimesheet", "Employee timesheet successfully updated!")
                    Toast.makeText(this, "Timesheet Update Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Timesheet Update Failed", Toast.LENGTH_SHORT).show()
                    Log.e("UpdateTimesheet", "Failed to update Timesheet", exception)
                }
        } else if (totalHours > 50) {
            Toast.makeText(this, "Timesheet cannot be submitted, work hours more than 50 for the week", Toast.LENGTH_SHORT).show()
        } else if (totalHours < 50 && totalHours > 40) {
            submitOvertime(datePeriod, updatedMon, updatedTue, updatedWed, updatedThu, updatedFri)
            Toast.makeText(this@SubmitWeekTimesheetActivity, "Overtime successfully submitted", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeToFirebase(datePeriod: Calendar, monHours: Double, tueHours: Double, wedHours: Double, thuHours: Double, friHours: Double) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        val timesheetKey = getTimesheetKey(userNum, datePeriod)

        val database = Firebase.database
        val dbRef = database.getReference("SparkLineHR")
        val entry = Timesheet(monHours, tueHours, wedHours, thuHours, friHours)

        val totalHours = monHours + tueHours + wedHours + thuHours + friHours


        if (totalHours < 40) {
            dbRef.child("Timesheet entry").child(timesheetKey).setValue(entry)
                .addOnSuccessListener {
                    Log.d("LogTimesheet", "Timesheet successfully recorded!")
                    Toast.makeText(this@SubmitWeekTimesheetActivity, "Timesheet successfully submitted", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LogTimesheet", "Database write failed", exception)
                }
        } else if (totalHours > 50) {
            Toast.makeText(this, "Timesheet cannot be submitted, work hours more than 50 for the week", Toast.LENGTH_SHORT).show()
        } else if (totalHours < 50 && totalHours > 40) {
            submitOvertime(datePeriod, monHours, tueHours, wedHours, thuHours, friHours)
            Toast.makeText(this@SubmitWeekTimesheetActivity, "Overtime successfully submitted", Toast.LENGTH_LONG).show()
        }
    }

    private fun submitOvertime(weekKey: Calendar, monHours: Double, tueHours: Double, wedHours: Double, thuHours: Double, friHours: Double) {

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()


        val datePeriod = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(weekKey.time)
        val timesheetKey = "${userNum},${datePeriod}"

        val database = Firebase.database
        val dbRef = database.getReference("SparkLineHR")
        val entry = Timesheet(monHours, tueHours, wedHours, thuHours, friHours)


        dbRef.child("Overtime Requests").child(timesheetKey).setValue(entry)
            .addOnSuccessListener {
                Log.d("LogOvertime", "Overtime Timesheet successfully recorded!")
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Database write failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("LogOvertime", "Database write failed", exception)
            }
    }




//    private fun setupSpinners() {
//        val hoursAdapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.hours_array,
//            android.R.layout.simple_spinner_item
//        )
//        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//        hoursSpinners.forEach { spinner ->
//            spinner.adapter = hoursAdapter
//        }
//    }

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

    private fun updateWeekDates(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val weekStart = calendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val weekEnd = calendar.clone() as Calendar
        weekEnd.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

        weekDatesTextView.text = "${dateFormat.format(weekStart.time)} - ${dateFormat.format(weekEnd.time)}"

        // Update spinners based on future dates or leave periods
        for (i in 0..4) {
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_WEEK, i)
            dayDateTextViews[i].text = dateFormat.format(day.time)
            hoursInput[i].isVisible = !day.after(Calendar.getInstance()) && !isDateWithinLeavePeriod(day)
        }
    }



    private fun loadLeavePeriods() {
        val database = Firebase.database
        val dbRef = database.getReference("SparkLineHR")

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        dbRef.child("Approved Leave Requests").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                leavePeriods.clear()

                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    if (key != null && key.startsWith(userNum)) {
                        val leaveRequest = childSnapshot.getValue(LeaveRequest::class.java)
                        if (leaveRequest != null) {
                            val startDate = Calendar.getInstance()
                            val endDate = Calendar.getInstance()

                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            startDate.time = dateFormat.parse(leaveRequest.FromDate)!!
                            endDate.time = dateFormat.parse(leaveRequest.ToDate)!!

                            leavePeriods.add(Pair(startDate, endDate))
                        }
                    }
                }
                updateWeekDates(displayedWeek)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_ERROR", "Failed to load leave periods: ${error.message}")
            }
        })
    }

    private fun isDateWithinLeavePeriod(date: Calendar): Boolean {
        // Check if the given date falls within any leave period
        return leavePeriods.any { leavePeriod ->
            val (start, end) = leavePeriod
            (date >= start && date <= end)
        }
    }




    private fun getFormattedDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getTimesheetKey(userNum: String, datePeriod: Calendar): String {
        val formattedDate = getFormattedDate(datePeriod)
        return "$userNum,$formattedDate"
    }





    data class LeaveRequest(val LeaveType: String = "",
                            val FromDate: String = "",
                            val ToDate: String = "",
                            val Document: String = "")


    data class Timesheet(val monHours: Double = 0.0,
                         val tueHours: Double = 0.0,
                         val wedHours: Double = 0.0,
                         val thuHours: Double = 0.0,
                         val friHours: Double = 0.0)
}