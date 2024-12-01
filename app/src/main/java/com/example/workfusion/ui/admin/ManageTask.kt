package com.example.workfusion.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workfusion.adapter.TaskAdapter
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.databinding.FragmentManageTaskBinding
import com.example.workfusion.utils.TaskViewModelFactory
import com.example.workfusion.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageTask : Fragment() {
    private var _binding: FragmentManageTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageTaskBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeTasks()

        return binding.root
    }

    private fun observeTasks() {
        taskViewModel.fetchTasks()
        taskViewModel.taskList.observe(viewLifecycleOwner) { taskList ->
            Toast.makeText(requireContext(), "Tasks Count: ${taskList.size}", Toast.LENGTH_SHORT).show()
            taskAdapter = TaskAdapter(taskList)
            binding.rvManagetaskAdmin.adapter = taskAdapter
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList())
        binding.rvManagetaskAdmin.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageTask().apply {
                arguments = Bundle().apply {
                    putString("ARG_PARAM1", param1)
                    putString("ARG_PARAM2", param2)
                }
            }
    }
}
