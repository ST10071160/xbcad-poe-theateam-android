package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrainingAdapter(
    private val trainingList: List<TrainingActivity.TrainingInfo>,
    private val onTrainingClick: (TrainingActivity.TrainingInfo) -> Unit
) : RecyclerView.Adapter<TrainingAdapter.TrainingViewHolder>() {

    inner class TrainingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trainingName: TextView = view.findViewById(R.id.training_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.training_item, parent, false)
        return TrainingViewHolder(view)
    }

    //this will display the training course name back to the user to be selected from the list
    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val trainingInfo = trainingList[position]
        holder.trainingName.setText(trainingInfo.CourseName)
        holder.itemView.setOnClickListener { onTrainingClick(trainingInfo) }
    }

    override fun getItemCount(): Int {
        return trainingList.size
    }
}