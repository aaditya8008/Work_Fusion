package com.example.workfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {
    private val _allEmployees = MutableStateFlow<List<Map<String, Any>>?>(null)
    val allEmployees: StateFlow<List<Map<String, Any>>?> get() = _allEmployees

    private val _organizationId = MutableStateFlow<String?>(null)
    val organizationId: StateFlow<String?> get() = _organizationId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    /**
     * Fetch the organization ID and update the state flow.
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
     * Fetch all employees after ensuring the organization ID is retrieved.
     */
    fun fetchAllEmployees() {
        viewModelScope.launch {
            try {
                // Ensure organization ID is fetched
                val orgId = _organizationId.value ?: run {
                    fetchOrganizationId()
                    // Wait for the organization ID to be updated
                    _organizationId.value ?: throw Exception("Organization ID not found.")
                }

                // Fetch employees based on the organization ID
                val employees = repository.getAllEmployeeDetails(orgId)
                _allEmployees.value = employees
            } catch (e: Exception) {
                _error.value = "Failed to fetch all employees: ${e.message}"
            }
        }
    }

    /**
     * Clear the current error state.
     */
    fun clearError() {
        _error.value = null
    }
}
