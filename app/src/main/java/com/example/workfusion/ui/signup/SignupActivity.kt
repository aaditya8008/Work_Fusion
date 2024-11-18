package com.example.workfusion.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.workfusion.data.repository.UserRepository
import com.example.workfusion.databinding.ActivitySignupBinding
import com.example.workfusion.utils.ViewModelFactory
import com.example.workfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userViewModel: AuthViewModel by viewModels { ViewModelFactory(UserRepository(auth, db)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.organizationSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.organizationSwitch.text = if (isChecked) "Admin" else "Employee"
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.signupButton.setOnClickListener {
            val organizationName = binding.organizationNameEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val phoneNumber = binding.phoneEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val userType = if (binding.organizationSwitch.isChecked) "organization" else "employee"

            if (binding.organizationSwitch.isChecked) {
                // Organization signup
                if (organizationName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    registerOrganization(organizationName, email, password,userType)
                } else {
                    Toast.makeText(this, "Please fill all organization fields", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Employee signup
                if (organizationName.isNotEmpty() && name.isNotEmpty() && email.isNotEmpty() && phoneNumber.isNotEmpty() && password.isNotEmpty()) {
                    registerEmployee(organizationName, name, email, phoneNumber, password,userType)
                } else {
                    Toast.makeText(this, "Please fill all employee fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerOrganization(organizationName: String, email: String, password: String,userType:String) {
        userViewModel.signupOrganization(organizationName, email, password,userType,
            onSuccess = { authResult ->
                sendEmailVerification(authResult.user?.email)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Organization signup failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun registerEmployee(organizationName: String, name: String, email: String, phoneNumber: String, password: String,userType:String) {
        userViewModel.signupEmployee(organizationName, name, email, phoneNumber, password,userType,
            onSuccess = { authResult ->
                sendEmailVerification(authResult.user?.email)
            },
            onFailure = { exception ->
                Toast.makeText(this, "Employee signup failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun sendEmailVerification(email: String?) {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent to $email", Toast.LENGTH_LONG).show()
                navigateToVerificationScreen()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToVerificationScreen() {
        val intent=Intent(this, VerificationActivity::class.java)
        intent.putExtra("userType",binding.organizationSwitch.isChecked)
        startActivity(Intent(this, VerificationActivity::class.java))
        finish()
    }
}