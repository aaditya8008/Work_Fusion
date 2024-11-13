package com.example.workfusion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.workfusion.ui.Home
import com.example.workfusion.ui.admin.HomeAdmin
import com.example.workfusion.ui.employee.HomeEmployee
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SplashScreen : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splashscreem)

        // Set up edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delay for splash screen display
        Handler(Looper.getMainLooper()).postDelayed({
            firebaseAuth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Perform user identification after a delay
                lifecycleScope.launch {
                    identifyUser(firebaseAuth, db)
                }
            } else {
                // If no user is logged in, navigate to the main screen
                startActivity(Intent(this, Home::class.java))
                finish()
            }
        }, 3000)
    }

    // Function to identify user and navigate to the appropriate screen
    private suspend fun identifyUser(firebaseAuth: FirebaseAuth, db: FirebaseFirestore) {
        val currentUser = firebaseAuth.currentUser

        try {
            // Fetch organization data from Firestore asynchronously
            val orgDoc = db.collection("organizations")
                .document(firebaseAuth.currentUser!!.uid)
                .get()
                .await()

            // Navigate based on user type
            if (orgDoc.exists()) {
                startActivity(Intent(this, HomeAdmin::class.java))
            } else {
                startActivity(Intent(this, HomeEmployee::class.java))
            }
        } catch (e: Exception) {
            // Handle Firestore or network-related errors
            e.printStackTrace()  // Log error for debugging
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, Home::class.java))  // Fallback to employee home
        } finally {
            finish() // Finish the splash screen activity
        }
    }
}

