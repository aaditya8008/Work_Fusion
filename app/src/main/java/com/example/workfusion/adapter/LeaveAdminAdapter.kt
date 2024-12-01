package com.example.workfusion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workfusion.databinding.RvLeaveApplicationsBinding
import com.example.workfusion.model.Leave
import com.example.workfusion.viewmodel.LeaveViewModel

class LeaveAdminAdapter(
    private val leaveList: List<Leave>,
    private val leaveViewModel: LeaveViewModel
) : RecyclerView.Adapter<LeaveAdminAdapter.LeaveViewHolder>() {

    class LeaveViewHolder(val binding: RvLeaveApplicationsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = RvLeaveApplicationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun getItemCount() = leaveList.size

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val leave = leaveList[position]

        holder.binding.apply {
            employeeName.text = leave.name
            sno.text = leave.sn.toString()
            typeOfLeave.text = leave.type
            status.text = leave.status
            message.text = leave.reason

            acceptButton.setOnClickListener {
                leaveViewModel.updateStatus(leave.leaveId, "Accepted") // Call ViewModel's update function

            }
            rejectButton.setOnClickListener {
                leaveViewModel.updateStatus(leave.leaveId, "Rejected")

            }
        }
    }

}
