package com.example.workfusion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.LeaveRepository
import com.example.workfusion.model.Leave
import kotlinx.coroutines.launch

class LeaveViewModel(private val leaveRepository: LeaveRepository) : ViewModel() {

    private val _uploadLeaveResult = MutableLiveData<Result<String>>()
    val uploadLeaveResult: LiveData<Result<String>> get() = _uploadLeaveResult

    // MutableLiveData to hold the list of leaves
    private val _leaveList = MutableLiveData<List<Leave>>()
    val leaveList: LiveData<List<Leave>> get() = _leaveList

    // Function to upload a leave
    fun uploadLeave(
        empId: Long,
        name: String,
        reason: String,
        startDate: String,
        endDate: String,
        subject: String,
        type: String,
        orgId: String
    ) {
        viewModelScope.launch {
            try {
                Log.d("LEAVE","view model $empId $name $reason $startDate $endDate")
                leaveRepository.uploadLeave(empId,orgId, name, reason, startDate, endDate,subject,type)
                _uploadLeaveResult.value = Result.success("Leave uploaded successfully")
            } catch (e: Exception) {
                _uploadLeaveResult.value = Result.failure(e)
            }
        }
    }

    // Function to fetch all leaves
    fun fetchLeaves() {
        viewModelScope.launch {
            try {
                // Fetch leaves from the repository and post them to LiveData
                val leaves = leaveRepository.fetchAllLeaves()
                _leaveList.postValue(leaves)
            } catch (e: Exception) {
                // Handle errors (e.g., log them or show an error state)
                e.printStackTrace()
                _leaveList.postValue(emptyList()) // Clear leave list on failure
            }
        }
    }
    fun updateStatus(leaveId: Long, newStatus: String){
        viewModelScope.launch {
        try {

                leaveRepository.updateLeaveStatus(leaveId,newStatus)


        }
        catch (e: Exception) {
            // Handle errors (e.g., log them or show an error state)
            e.printStackTrace()

        }}

    }

    // Function to fetch leaves for a specific employee
    fun fetchLeavesForEmployee() {
        viewModelScope.launch {
            try {
                // Fetch leaves from the repository and post them to LiveData
                val leaves = leaveRepository.fetchLeavesForEmployee()
                _leaveList.postValue(leaves)
            } catch (e: Exception) {
                // Handle errors (e.g., log them or show an error state)
                e.printStackTrace()
                _leaveList.postValue(emptyList()) // Clear leave list on failure
            }
        }
    }
}
