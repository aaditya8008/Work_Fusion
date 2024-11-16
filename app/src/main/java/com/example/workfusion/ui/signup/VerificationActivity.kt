package com.example.workfusion.ui.signup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.workfusion.LoadingFragement
import com.example.workfusion.MainActivity
import com.example.workfusion.R
import com.example.workfusion.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment = LoadingFragement()

// Get the FragmentManager
        val fragmentManager = supportFragmentManager

// Begin the transaction
        val transaction = fragmentManager.beginTransaction()

// Replace the container with the fragment
        transaction.replace(R.id.fragmentView, fragment)

// Commit the transaction
        transaction.commit()

        // Check email verification every 5 seconds
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                auth.currentUser?.reload()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (auth.currentUser?.isEmailVerified == true) {
                            Toast.makeText(this@VerificationActivity, "Email verified!", Toast.LENGTH_SHORT).show()
                            navigateToHomeScreen()
                        } else {
                            handler.postDelayed(this, 5000)
                        }
                    }
                }
            }
        }, 5000)
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}