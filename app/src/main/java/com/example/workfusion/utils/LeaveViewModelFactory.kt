package com.example.workfusion.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workfusion.data.repository.LeaveRepository
import com.example.workfusion.viewmodel.LeaveViewModel

class LeaveViewModelFactory(private val leaveRepository: LeaveRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaveViewModel::class.java)) {
            return LeaveViewModel(leaveRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
