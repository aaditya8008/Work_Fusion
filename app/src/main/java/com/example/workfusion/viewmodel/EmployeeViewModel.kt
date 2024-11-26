package com.example.workfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _organizationId = MutableStateFlow<String?>(null)
    val organizationId: StateFlow<String?> get() = _organizationId

    private val _allEmployees = MutableStateFlow<List<Map<String, Any>>?>(null)
    val allEmployees: StateFlow<List<Map<String, Any>>?> get() = _allEmployees

    private val _employeeDetails = MutableStateFlow<Map<String, Any>?>(null)
    val employeeDetails: StateFlow<Map<String, Any>?> get() = _employeeDetails

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    /**
     * Fetch the organization ID from the repository.
     */
    fun fetchOrganizationId() {
        viewModelScope.launch {
            try {
                val orgId = repository.getOrganizationId()
                _organizationId.value = orgId
            } catch (e: Exception) {
                _error.value = "Failed to fetch organization ID: ${e.message}"
            }
        }
    }

    /**
     * Fetch all employees of the organization.
     */
    fun fetchAllEmployees() {
        viewModelScope.launch {
            try {
                val orgId = _organizationId.value ?: run {
                    fetchOrganizationId()
                    _organizationId.value ?: throw Exception("Organization ID not available.")
                }

                val employees = repository.getAllEmployeeDetails(orgId)
                _allEmployees.value = employees
            } catch (e: Exception) {
                _error.value = "Failed to fetch employees: ${e.message}"
            }
        }
    }

    /**
     * Fetch details of a specific employee by their ID.
     */
    fun fetchEmployeeDetails(employeeId: String) {
        viewModelScope.launch {
            try {
                val details = repository.getEmpDetail(employeeId)
                _employeeDetails.value = details
            } catch (e: Exception) {
                _error.value = "Failed to fetch employee details: ${e.message}"
            }
        }
    }


    /**
     * Clear the error state to allow retries or fresh data.
     */
    fun clearError() {
        _error.value = null
    }
}
