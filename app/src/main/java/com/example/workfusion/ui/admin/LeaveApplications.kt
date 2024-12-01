package com.example.workfusion.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workfusion.adapter.LeaveAdminAdapter
import com.example.workfusion.data.repository.LeaveRepository
import com.example.workfusion.databinding.FragmentLeaveApplicationsBinding
import com.example.workfusion.model.Leave
import com.example.workfusion.utils.LeaveViewModelFactory
import com.example.workfusion.viewmodel.LeaveViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LeaveApplications : Fragment() {

    private var _binding: FragmentLeaveApplicationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaveAdapter: LeaveAdminAdapter
    private val leaveViewModel: LeaveViewModel by viewModels {
        LeaveViewModelFactory(LeaveRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaveApplicationsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeLeaves()
        return binding.root
    }

    private fun setupRecyclerView() {
        leaveAdapter = LeaveAdminAdapter(emptyList(),leaveViewModel)
        binding.rvLeaveAdmin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaveAdapter
        }
    }

    private fun observeLeaves() {
        leaveViewModel.fetchLeaves()
        leaveViewModel.leaveList.observe(viewLifecycleOwner) { leaveList ->
            Toast.makeText(requireContext(), "Total Leaves: ${leaveList.size}", Toast.LENGTH_SHORT).show()
            leaveAdapter = LeaveAdminAdapter(leaveList,leaveViewModel)
            binding.rvLeaveAdmin.adapter = leaveAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
