package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OvertimeAdapter(
    private val overtimeList: List<String>
) : RecyclerView.Adapter<OvertimeAdapter.OvertimeViewHolder>() {

    inner class OvertimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val overtimeDate: TextView = view.findViewById(R.id.overtime_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OvertimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.overtime_item, parent, false)
        return OvertimeViewHolder(view)
    }

    //this will display the overtime request date back to the user to be selected from the list
    override fun onBindViewHolder(holder: OvertimeViewHolder, position: Int) {
        val overtimereq = overtimeList[position]
        holder.overtimeDate.setText(overtimereq)
    }

    override fun getItemCount(): Int {
        return overtimeList.size
    }
}