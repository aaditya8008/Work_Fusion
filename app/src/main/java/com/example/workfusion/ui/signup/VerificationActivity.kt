package com.example.workfusion.ui.signup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workfusion.LoadingFragement
import com.example.workfusion.R
import com.example.workfusion.databinding.ActivityVerificationBinding
import com.example.workfusion.ui.admin.HomeAdmin
import com.example.workfusion.ui.employee.HomeEmployee
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private val auth = FirebaseAuth.getInstance()
    private var organization: Boolean = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        organization = intent.getBooleanExtra("userType", false)
        val fragment = LoadingFragement()

        // Add the loading fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentView, fragment)
        transaction.commit()

        checkEmailVerificationWithTimeout()
    }



    override fun onBackPressed() {
        super.onBackPressed()
        handleUserRemoval()
    }

    private fun checkEmailVerificationWithTimeout() {
        var elapsedTime = 0L
        val checkInterval = 5000L
        val timeout = 60000L

        handler.postDelayed(object : Runnable {
            override fun run() {
                auth.currentUser?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            Toast.makeText(
                                this@VerificationActivity,
                                "Email verified!",
                                Toast.LENGTH_SHORT
                            ).show()
                            handler.removeCallbacksAndMessages(null) // Stop further checks
                            if (organization) {
                                navigateToAdminHomeScreen()
                            } else {
                                navigateToEmpHomeScreen()
                            }
                        } else {
                            elapsedTime += checkInterval
                            if (elapsedTime >= timeout) {
                                handleUserRemoval()
                                Toast.makeText(
                                    this@VerificationActivity,
                                    "Email not verified within 1 minute. Logged out.",
                                    Toast.LENGTH_LONG
                                ).show()
                                navigateToSignUpScreen()
                            } else {
                                handler.postDelayed(this, checkInterval)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@VerificationActivity,
                            "Failed to check email verification status.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }, checkInterval)
    }

    private fun handleUserRemoval() {
        auth.currentUser?.let { user ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    user.delete().await()
                    auth.signOut()
                    Toast.makeText(this@VerificationActivity, "User removed due to inactivity.", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@VerificationActivity, "Failed to remove user: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToEmpHomeScreen() {
        val intent = Intent(this, HomeEmployee::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAdminHomeScreen() {
        val intent = Intent(this, HomeAdmin::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUpScreen() {
        val intent = Intent(this, SignupActivity::class.java) // Replace with your login activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
