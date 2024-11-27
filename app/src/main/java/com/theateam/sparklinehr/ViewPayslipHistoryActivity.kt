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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theateam.sparklinehr.databinding.ActivityViewPayslipHistoryBinding

class ViewPayslipHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPayslipHistoryBinding

    private var payslipList = ArrayList<Payslip>()


    private lateinit var recyclerView: RecyclerView
    private lateinit var payslipAdapter: PayslipAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityViewPayslipHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_view_payslip_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.viewPayslipHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadPayslips()

        payslipAdapter = PayslipAdapter(payslipList) { payslip ->
            if (payslip != null) {
                val intent = Intent(this, ViewSelectedPayslipActivity::class.java)
                intent.putExtra("payslip", payslip)
                Log.d("PayslipIntent", "Passing Payslip: $payslip") // Debugging
                startActivity(intent)
            } else {
                Log.e("PayslipIntent", "Payslip is null!")
            }
        }

        recyclerView.adapter = payslipAdapter

        binding.backBtn.setOnClickListener{
            finish()
        }
    }


    //this method will load all of the payslip objects related to the current user from the firebase and display it in a list
    private fun loadPayslips() {
        val database = FirebaseDatabase.getInstance()
        val dbRef = database.getReference("SparkLineHR")


        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val userNum = sharedPreferences.getString("EMPLOYEE_ID", null).toString()

        // Fetch all payslips for the given employee
        dbRef.child("payslips").child(userNum).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing data
                    payslipList.clear()

                    for (childSnapshot in dataSnapshot.children) {
                        val key = childSnapshot.key
                        if (key != null) {
                            val payslip = childSnapshot.getValue(Payslip::class.java)
                            if (payslip != null) {
                                payslipList.add(payslip)
                                Log.d("PayslipInfo", "Payslip found: $payslip")
                            } else {
                                Log.e("PayslipInfo", "No data found for key: $key")
                            }
                        }
                    }

                    // Notify the adapter to update the UI
                    payslipAdapter.notifyDataSetChanged()
                    Log.d("FIREBASE_SUCCESS", "Payslips fetched successfully: $payslipList")
                } else {
                    Log.e("FIREBASE_ERROR", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FIREBASE_FAILURE", "Failed to fetch data: ${error.message}")
            }
        })
    }


}