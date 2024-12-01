package com.example.workfusion.ui.employee

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workfusion.R
import com.example.workfusion.adapter.TaskAdapter
import com.example.workfusion.adapter.TaskEmpAdapter
import com.example.workfusion.data.repository.TaskRepository
import com.example.workfusion.databinding.FragmentAddTaskBinding
import com.example.workfusion.databinding.FragmentUpdateTasksBinding
import com.example.workfusion.utils.TaskViewModelFactory
import com.example.workfusion.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UpdateTasks : Fragment() {
    private var _binding: FragmentUpdateTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskEmpAdapter
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
    ): View? {
        _binding=FragmentUpdateTasksBinding.inflate(inflater,container,false)
        setupRecyclerView()
        observeTasks()
        return binding.root
    }

    private fun observeTasks() {
        taskViewModel.fetchTasksForEmployee()
        taskViewModel.taskList.observe(viewLifecycleOwner){taskList->
            Toast.makeText(requireContext(),"${taskList.size}",Toast.LENGTH_SHORT).show()
            taskAdapter = TaskEmpAdapter(taskList,taskViewModel)
            binding.rvTaskEmp.adapter = taskAdapter

        }

    }

    private fun setupRecyclerView() {
       taskAdapter = TaskEmpAdapter(emptyList(),taskViewModel)
        binding.rvTaskEmp.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter=taskAdapter
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateTasks().apply {
                arguments = Bundle().apply {

                }
            }
    }
}