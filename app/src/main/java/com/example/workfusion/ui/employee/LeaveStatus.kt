package com.example.workfusion.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workfusion.adapter.LeaveAdapter
import com.example.workfusion.adapter.TaskAdapter
import com.example.workfusion.data.repository.LeaveRepository
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.databinding.FragmentLeaveStatusBinding
import com.example.workfusion.model.Leave
import com.example.workfusion.utils.LeaveViewModelFactory
import com.example.workfusion.utils.TaskViewModelFactory
import com.example.workfusion.viewmodel.LeaveViewModel
import com.example.workfusion.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaveStatus : Fragment() {

    private var _binding: FragmentLeaveStatusBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaveAdapter: LeaveAdapter
    private val leaveViewModel: LeaveViewModel by viewModels {
        LeaveViewModelFactory(LeaveRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaveStatusBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeTasks()
        return binding.root
    }

    private fun setupRecyclerView() {
        leaveAdapter = LeaveAdapter(emptyList())
        binding.rvLeaveStatus.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaveAdapter
        }
    }
    private fun observeTasks() {
        leaveViewModel.fetchLeavesForEmployee()
        leaveViewModel.leaveList.observe(viewLifecycleOwner){leaveList->
            Toast.makeText(requireContext(),"${leaveList.size}", Toast.LENGTH_SHORT).show()
            leaveAdapter = LeaveAdapter(leaveList)
            binding.rvLeaveStatus.adapter = leaveAdapter

        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
