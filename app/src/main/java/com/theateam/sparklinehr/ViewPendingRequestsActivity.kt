package com.theateam.sparklinehr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityViewPendingRequestsBinding

class ViewPendingRequestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPendingRequestsBinding

    private var leaveRequestList = ArrayList<LeaveRequest>()
    private var overtimeRequestList = ArrayList<String>()


    private lateinit var overtimeRecyclerView: RecyclerView
    private lateinit var overtimeAdapter: OvertimeAdapter

    private lateinit var leaveReqRecyclerView: RecyclerView
    private lateinit var leaveReqAdapter: LeaveReqAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewPendingRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_view_pending_requests)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        overtimeRecyclerView = findViewById(R.id.viewOvertimeRequestRecyclerView)
        overtimeRecyclerView.layoutManager = LinearLayoutManager(this)

        leaveReqRecyclerView = findViewById(R.id.leaveRequestRecyclerView)
        leaveReqRecyclerView.layoutManager = LinearLayoutManager(this)

        loadOvertimeRequests()
        loadLeaveRequests()

        overtimeAdapter = OvertimeAdapter(overtimeRequestList)
        leaveReqAdapter = LeaveReqAdapter(leaveRequestList)

        overtimeRecyclerView.adapter = overtimeAdapter
        leaveReqRecyclerView.adapter = leaveReqAdapter

        binding.backBtn.setOnClickListener{
            finish()
        }
    }



    private fun loadOvertimeRequests() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Fetch all overtime requests for the given employee
        dbRef.child("Overtime Requests").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    overtimeRequestList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum)) {

                            val keyparts = key.split(",")
                            val overtimeRequestDate = keyparts[1]

                            if (overtimeRequestDate != null) {
                                overtimeRequestList.add(overtimeRequestDate)
                                Log.d("OvertimeInfo", "Overtime Request found: $overtimeRequestDate")
                            } else {
                                Log.e("OvertimeInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    overtimeAdapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Overtime Requests fetched successfully: $overtimeRequestList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }



    private fun loadLeaveRequests() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Fetch all pending leave requests for the given employee
        dbRef.child("Pending Leave Requests").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    leaveRequestList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null && key.startsWith(userNum)) {
                            val leaveRequest = childSnapshot.getValue(LeaveRequest::class.java)
                            if (leaveRequest != null) {
                                leaveRequestList.add(leaveRequest)
                                Log.d("LeaveReqInfo", "Leave Request found: $leaveRequest")
                            } else {
                                Log.e("LeaveReqInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    leaveReqAdapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Leave Requests fetched successfully: $leaveRequestList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }


    data class LeaveRequest(val leaveType: String = "",
                            val fromDate: String = "",
                            val toDate: String = "",
                            val document: String = "")
}