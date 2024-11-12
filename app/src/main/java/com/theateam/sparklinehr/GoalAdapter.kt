package com.theateam.sparklinehr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GoalAdapter(
    private val goals: List<String>,
    private val onDeleteClick: (String) -> Unit,
    private val onEditClick: (String) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goalTextView: TextView = view.findViewById(R.id.goalTextView)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goal_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalTextView.text = goal

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDeleteClick(goal)
        }

        // Handle edit button click
        holder.editButton.setOnClickListener {
            onEditClick(goal)
        }
    }

    override fun getItemCount(): Int {
        return goals.size
    }
}

