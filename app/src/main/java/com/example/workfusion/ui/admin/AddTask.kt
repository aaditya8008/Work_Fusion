package com.example.workfusion.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.workfusion.data.repository.EmployeeRepository
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.databinding.FragmentAddTaskBinding
import com.example.workfusion.utils.EmployeeViewModelFactory
import com.example.workfusion.utils.TaskViewModelFactory
import com.example.workfusion.viewmodel.EmployeeViewModel
import com.example.workfusion.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

class AddTask : Fragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    lateinit var repository: EmployeeRepository
    private val viewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(EmployeeRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }

    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        setupObservers()
        setupCalendarListeners()
        viewModel.fetchOrganizationId()
        viewModel.fetchAllEmployees()

        binding.create.setOnClickListener {
            val employee = binding.spinnerEmployee.selectedItem.toString().substringAfter(' ')
            val description = binding.descriptionInput.text.toString()



            val empId = binding.spinnerEmployee.selectedItem.toString().substringBefore('.').trim() // "1"
            Toast.makeText(requireContext(),"$empId",Toast.LENGTH_LONG).show()


            // Validate input fields
            if (startDate == null || endDate == null || description.isBlank() || empId.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {

                taskViewModel.uploadTask(
                    empId = empId.toLong(),
                    name = employee.substringAfter('.'),
                    description = description,
                    startDate = startDate!!,
                    endDate = endDate!!
                )
            }
        }

        return binding.root
    }

    private fun setupCalendarListeners() {
        binding.startDateCalender.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                binding.startDateCalender.text = selectedDate
            }
        }

        binding.endDateCalender.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                binding.endDateCalender.text = selectedDate
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        ).show()
    }

    // Convert string to Date




    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.allEmployees.collect { employees ->
                if (employees != null) {
                    val employeeDetails = employees.map { employee ->
                        val name = employee["name"] as String
                        val empId = employee["empId"] as Long
                        "$empId. $name"
                    }
                    setupSpinner(employeeDetails)
                }
            }

            viewModel.error.collect { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupSpinner(employeeNames: List<String>) {
        if (employeeNames.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                employeeNames
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEmployee.adapter = adapter
        } else {
            Toast.makeText(requireContext(), "No employees found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
