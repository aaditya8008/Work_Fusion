package com.example.workfusion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workfusion.databinding.RvManageTaskBinding
import com.example.workfusion.model.Task

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder using View Binding
    class TaskViewHolder(val binding: RvManageTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = RvManageTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Access views directly using binding
        holder.binding.apply {
            holder.binding.name.text=tasks.get(position).name
            holder.binding.description.text=tasks.get(position).description
            holder.binding.startDate.text=tasks.get(position).startDate
            holder.binding.endDate.text=tasks.get(position).endDate
            holder.binding.status.text=tasks.get(position).status
            holder.binding.taskId.text=tasks.get(position).taskId.toString()
        }
    }

    override fun getItemCount() = tasks.size
}
