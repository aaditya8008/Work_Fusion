package com.example.workfusion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workfusion.databinding.RvLeaveStatusBinding
import com.example.workfusion.model.Leave

class LeaveAdapter(private val leaves: List<Leave>) : RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder>() {

    // ViewHolder using View Binding
    class LeaveViewHolder(val binding: RvLeaveStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = RvLeaveStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val leave = leaves[position]

        // Access views directly using binding
        holder.binding.apply {
            sno.text=leave.sn.toString()
            type.text = leave.type
            status.text = leave.status
            message.text=leave.reason

        }
    }

    override fun getItemCount() = leaves.size
}
