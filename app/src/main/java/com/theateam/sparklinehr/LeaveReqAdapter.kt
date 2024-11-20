package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaveReqAdapter(
    private val leaveList: List<ViewPendingRequestsActivity.LeaveRequest>
) : RecyclerView.Adapter<LeaveReqAdapter.LeaveViewHolder>() {

    inner class LeaveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leaveDate: TextView = view.findViewById(R.id.leavereq_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leavereq_item, parent, false)
        return LeaveViewHolder(view)
    }

    //this will display the leave request date back to the user to be selected from the list
    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val leavereq = leaveList[position]
        holder.leaveDate.setText(leavereq.fromDate)
    }

    override fun getItemCount(): Int {
        return leaveList.size
    }
}