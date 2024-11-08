package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PayslipAdapter(
    private val payslipList: List<Payslip>,
    private val onRecipeClick: (Payslip) -> Unit
) : RecyclerView.Adapter<PayslipAdapter.PayslipViewHolder>() {

    inner class PayslipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val payslipMonth: TextView = view.findViewById(R.id.payslip_month)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayslipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.payslip_item, parent, false)
        return PayslipViewHolder(view)
    }

    //this will display the recipe name back to the user to be selected from the list
    override fun onBindViewHolder(holder: PayslipViewHolder, position: Int) {
        val payslip = payslipList[position]
        holder.payslipMonth.setText(payslip.payslipPeriod)
        holder.itemView.setOnClickListener { onRecipeClick(payslip) }
    }

    override fun getItemCount(): Int {
        return payslipList.size
    }
}