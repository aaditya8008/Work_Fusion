package com.example.workfusion.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workfusion.R
import com.example.workfusion.databinding.ActivityHomeAdminBinding
import com.example.workfusion.databinding.ActivityHomeEmployeeBinding
import com.example.workfusion.ui.Home
import com.example.workfusion.ui.employee.ApplyLeave
import com.example.workfusion.ui.employee.UpdateTasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class HomeAdmin : AppCompatActivity() {
    lateinit var binding: ActivityHomeAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewAdmin, AddTask())
                .commit()
            binding.bottomNavAdmin.selectedItemId = R.id.create_task_admin
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,Home::class.java))
            finish()

        }
        binding.bottomNavAdmin.setOnItemSelectedListener {item->
            val fragment=when(item.itemId){
                R.id.create_task_admin-> AddTask()
                R.id.manage_task_admin-> ManageTask()
                R.id.leaves_admin-> LeaveApplications()
                else->null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerViewAdmin, it)
                    .commit()
            }

            true
        }



    }
}