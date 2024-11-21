package com.example.workfusion.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.model.Task
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uploadTaskResult = MutableLiveData<Result<String>>()
    val uploadTaskResult: LiveData<Result<String>> get() = _uploadTaskResult

    // MutableLiveData to hold the list of tasks
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> get() = _taskList

    // Function to upload a task
    fun uploadTask(
        empId: Long,
        name: String,
        description: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                taskRepository.uploadTask(empId, name, description, startDate, endDate)
                _uploadTaskResult.value = Result.success("Task uploaded successfully")
            } catch (e: Exception) {
                _uploadTaskResult.value = Result.failure(e)
            }
        }
    }

    // Function to fetch all tasks
    fun fetchTasks(organizationId: String) {
        viewModelScope.launch {
            try {
                // Fetch tasks from the repository and post them to LiveData
                val tasks = taskRepository.fetchAllTasks()
                _taskList.postValue(tasks)
            } catch (e: Exception) {
                // Handle errors (e.g., log them or show an error state)
                e.printStackTrace()
                _taskList.postValue(emptyList()) // Clear task list on failure
            }
        }
    }
}
