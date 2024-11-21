package com.example.workfusion.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uploadTaskResult = MutableLiveData<Result<String>>()
    val uploadTaskResult: LiveData<Result<String>> get() = _uploadTaskResult

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
}
