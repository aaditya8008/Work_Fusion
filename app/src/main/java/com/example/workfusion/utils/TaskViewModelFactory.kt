package com.example.workfusion.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.viewmodel.EmployeeViewModel
import com.example.workfusion.viewmodel.TaskViewModel

class TaskViewModelFactory(private val taskRepository: TaskRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}