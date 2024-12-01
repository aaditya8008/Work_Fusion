package com.example.workfusion.ui.employee

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.workfusion.R
import com.example.workfusion.data.repository.EmployeeRepository
import com.example.workfusion.data.repository.LeaveRepository
import com.example.workfusion.databinding.FragmentApplyLeaveBinding
import com.example.workfusion.utils.EmployeeViewModelFactory
import com.example.workfusion.utils.LeaveViewModelFactory
import com.example.workfusion.viewmodel.EmployeeViewModel
import com.example.workfusion.viewmodel.LeaveViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

class ApplyLeave : Fragment() {
    private var _binding: FragmentApplyLeaveBinding? = null
    private var name: String? = null
    private var empId: Long = 0L
    private var orgId :String?=null
    private val binding get() = _binding!!
    private val viewModel: LeaveViewModel by viewModels {
        LeaveViewModelFactory(LeaveRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }
    private val empViewModel: EmployeeViewModel by viewModels {
        EmployeeViewModelFactory(EmployeeRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }

    private var startDate: String? = null
    private var endDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyLeaveBinding.inflate(inflater, container, false)
        setupCalendarListeners()
        empViewModel.fetchOrganizationId()
        empViewModel.fetchEmployeeDetails(FirebaseAuth.getInstance().currentUser!!.uid)
        setupObservers()
        setupSpinner()

        binding.submitLeaveButton.setOnClickListener {
            val reason = binding.leaveReasonInput.text.toString()
            val type=binding.spinnerLeaveType.selectedItem.toString()
            val subject=binding.subjectInput.text.toString()
            // Validate input fields and ensure name and empId are initialized
            if (startDate == null || endDate == null || reason.isBlank() || name.isNullOrEmpty() || empId == 0L) {
                Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
                Log.d("HOLLAA!!","$name $empId $startDate $endDate $reason")
            } else {

                viewModel.uploadLeave(
                    empId = empId,
                    orgId=orgId!!,
                    name = name!!,
                    startDate = startDate!!,
                    endDate = endDate!!,
                    subject=subject,
                    reason = reason,
                    type=type

                )
            }
        }

        return binding.root
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinnerLeaveType
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_items,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
    }

    private fun setupCalendarListeners() {
        binding.startDateCalenderLeave.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                binding.startDateCalenderLeave.text = selectedDate
            }
        }

        binding.endDateCalenderLeave.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                binding.endDateCalenderLeave.text = selectedDate
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

    private fun setupObservers() {
        lifecycleScope.launch {
            empViewModel.employeeDetails.collect { employees ->
                if(employees!=null) {
                    employees?.let {
                        try {
                            name = it["name"] as? String
                            empId = it["empId"] as? Long ?: 0L
                            orgId=it["orgId"] as? String
                            println("Employee Name: $name, Employee ID: $empId")
                        } catch (e: Exception) {
                            println("Error fetching employee details: ${e.message}")
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(),"Null employee",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

