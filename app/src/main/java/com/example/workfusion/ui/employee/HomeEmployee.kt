package com.example.workfusion.ui.employee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.workfusion.R
import com.example.workfusion.databinding.ActivityHomeBinding
import com.example.workfusion.databinding.ActivityHomeEmployeeBinding

class HomeEmployee : AppCompatActivity() {
    lateinit var binding: ActivityHomeEmployeeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeEmployeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewEmp, UpdateTasks())
                .commit()
            binding.bottomNavEmp.selectedItemId = R.id.task_emp
        }
        binding.bottomNavEmp.setOnItemSelectedListener {item->
            val fragment=when(item.itemId){
                R.id.task_emp->UpdateTasks()
                R.id.apply_leave_emp->ApplyLeave()
                R.id.leaves_emp->LeaveStatus()
                else->null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerViewEmp, it)
                    .commit()
            }

            true

        }

    }
}