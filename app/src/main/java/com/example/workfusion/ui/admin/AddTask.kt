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
import com.example.workfusion.R
import com.example.workfusion.data.repository.EmployeeRepository
import com.example.workfusion.databinding.FragmentAddTaskBinding
import com.example.workfusion.utils.EmployeeViewModelFactory
import com.example.workfusion.viewmodel.EmployeeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AddTask : Fragment() {
    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    lateinit var repository: EmployeeRepository
    private val viewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(EmployeeRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        setupObservers()
        setupCalendarListeners()
        viewModel.fetchOrganizationId() // Fetch organization ID
        viewModel.fetchAllEmployees() // Fetch the list of employees

        return binding.root
    }

    /**
     * Setup listeners for calendar TextViews.
     */
    private fun setupCalendarListeners() {
        // Listener for Start Date
        binding.startDateCalender.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.startDateCalender.text = selectedDate
            }
        }

        // Listener for End Date
        binding.endDateCalender.setOnClickListener {
            showDatePicker { selectedDate ->
                binding.endDateCalender.text = selectedDate
            }
        }
    }

    /**
     * Function to show a DatePickerDialog.
     */
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        ).show()
    }

    /**
     * Setup observers to listen to employee data and populate the spinner.
     */
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

    /**
     * Set up the spinner with the list of employee names.
     */
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
