package com.example.workfusion.adapter

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workfusion.databinding.RvTaskManageUserBinding
import com.example.workfusion.model.Task
import com.example.workfusion.viewmodel.TaskViewModel

class TaskEmpAdapter(private val tasks: List<Task>,
                     private val taskViewModel: TaskViewModel) : RecyclerView.Adapter<TaskEmpAdapter.TaskViewHolder>() {

    // ViewHolder using View Binding
    class TaskViewHolder(val binding: RvTaskManageUserBinding) : RecyclerView.ViewHolder(binding.root)

    // Initialize the adapter for the spinner outside of onBindViewHolder to improve performance
    private val taskList = listOf("Not Started", "In Progress", "Completed")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = RvTaskManageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Access views directly using binding
        holder.binding.apply {
            snoemp.text = task.taskId.toString()
            startDateemp.text = task.startDate
            endDateemp.text = task.endDate
            status.text = task.status
            descriptionEmp.text = task.description

            // Set spinner adapter using the context of the view
            val adapter = ArrayAdapter(
                root.context, // Use root.context instead of getSystemClassLoader
                R.layout.simple_spinner_item,
                taskList
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spinnerEmptask.adapter = adapter

            // Set the current status of the task to the spinner
            val statusPosition = taskList.indexOf(task.status)
            if (statusPosition >= 0) {
                spinnerEmptask.setSelection(statusPosition)
            }

            updateButton.setOnClickListener {
                val selectedStatus = spinnerEmptask.selectedItem?.toString() ?: "No task selected"
                taskViewModel.updateStatus(task.taskId, selectedStatus)
            }
        }
    }

    override fun getItemCount() = tasks.size
}
