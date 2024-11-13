package com.example.workfusion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workfusion.data.repository.UserRepository
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Sign up organization
    fun signupOrganization(
        organizationName: String, email: String, password: String,userType: String,
        onSuccess: (AuthResult) -> Unit, onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.signupOrganization(organizationName, email, password,userType).getOrThrow()
                onSuccess(result)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Sign up employee
    fun signupEmployee(
        organizationName: String, name: String, email: String,
        phoneNumber: String, password: String,userType:String,
        onSuccess: (AuthResult) -> Unit, onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.signupEmployee(
                    organizationName, name, email, phoneNumber, password,userType
                ).getOrThrow()
                onSuccess(result)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Login employee/organization
    fun login(
        email: String, password: String,
        onSuccess: (AuthResult) -> Unit, onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = userRepository.login(email, password).getOrThrow()
                onSuccess(result)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
