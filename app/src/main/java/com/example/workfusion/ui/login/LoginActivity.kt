package com.example.workfusion.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.workfusion.MainActivity
import com.example.workfusion.R
import com.example.workfusion.data.repository.UserRepository
import com.example.workfusion.databinding.ActivityLoginBinding

import com.example.workfusion.utils.ViewModelFactory
import com.example.workfusion.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val userViewModel: AuthViewModel by viewModels { ViewModelFactory(UserRepository(auth, db)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val isOrganizationLogin = binding.loginRoleSwitch.isChecked

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call ViewModel to perform login
                userViewModel.login(email, password,
                    onSuccess = { authResult ->
                        // Navigate based on role selection
                        if (isOrganizationLogin) {
                            navigateToAdminScreen()  // Organization login
                        } else {
                            navigateToHomeScreen()   // Employee login
                        }
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAdminScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
