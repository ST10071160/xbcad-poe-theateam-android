package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PerformanceAdapter(
    private val reviewList: List<PerformanceActivity.PerformanceReview>,
    private val onReviewClick: (PerformanceActivity.PerformanceReview) -> Unit
) : RecyclerView.Adapter<PerformanceAdapter.PerformanceViewHolder>() {

    inner class PerformanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewDate: TextView = view.findViewById(R.id.review_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.performance_item, parent, false)
        return PerformanceViewHolder(view)
    }

    //this will display the performance review date back to the user to be selected from the list
    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        val performanceReview = reviewList[position]
        holder.reviewDate.setText(performanceReview.reviewDate)
        holder.itemView.setOnClickListener { onReviewClick(performanceReview) }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}