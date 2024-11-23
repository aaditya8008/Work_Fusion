package com.example.workfusion.data.repository

import android.util.Log
import com.example.workfusion.model.Leave
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class LeaveRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun uploadLeave(
        empId: Long, name: String, reason: String,
        startDate: String, endDate: String
    ) {
        try {
            val organizationId = auth.currentUser?.uid ?: throw Exception("Unable to get Id")
            val org = db.collection("organizations").document(organizationId)

            // Get the current leave counter and increment it
            val documentSnapshot = org.get().await()
            if (documentSnapshot.exists()) {
                val leaveCounter = documentSnapshot.getLong("leaveCounter") ?: 0L

                // Increment the counter and convert to Long safely
                val leaveId = leaveCounter + 1

                // Update the leave counter in Firestore
                org.update("leaveCounter", leaveCounter + 1).await()

                // Prepare leave data
                val leaveData = hashMapOf(
                    "leaveId" to leaveId,
                    "empId" to empId,
                    "name" to name,
                    "reason" to reason,
                    "status" to "Pending",
                    "startDate" to startDate,
                    "endDate" to endDate,
                    "organizationId" to organizationId
                )

                // Upload leave to Firestore
                db.collection("organizations").document(organizationId)
                    .collection("leaves")
                    .document("$empId.$name")
                    .set(leaveData)
                    .await()
                db.collection("leaves")
                    .document("$empId.$name")
                    .set(leaveData)
                    .await()

                Log.d("Firestore", "Leave successfully uploaded with leaveId: $leaveId")
            } else {
                throw Exception("Organization document does not exist")
            }
        } catch (e: Exception) {
            throw Exception("Leave Upload Failed: ${e.message}")
        }
    }

    suspend fun fetchAllLeaves(): List<Leave> {
        val organizationId = auth.currentUser?.uid ?: throw Exception("Unable to Fetch leaves")
        val leaveList = mutableListOf<Leave>()

        try {
            // Get all documents in the "leaves" collection
            val querySnapshot: QuerySnapshot = db.collection("organizations")
                .document(organizationId)
                .collection("leaves")
                .get()
                .await()

            // Convert each document to Leave
            for (document in querySnapshot.documents) {
                val leave = document.toObject(Leave::class.java)
                if (leave != null) {
                    leaveList.add(leave)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return leaveList
    }

    suspend fun fetchLeavesForEmployee(): List<Leave> {
        val empUid = auth.currentUser?.uid ?: throw Exception("Unable to Fetch leave: No authenticated user")
        val leaveList = mutableListOf<Leave>()

        try {
            // Fetch organizationId for the employee
            val documentSnapshot = db.collection("employees")
                .document(empUid) // Use the employee's UID to get employee data
                .get()
                .await()

            // Check if the organizationId and empId exist
            val organizationId = documentSnapshot.getString("organizationId")
                ?: throw Exception("Organization ID not found for employee: $empUid")
            val empId = documentSnapshot.getLong("empId")
                ?: throw Exception("Employee ID not found for employee")

            // Fetch leaves for the specific employee
            val querySnapshot = db.collection("leaves")
                .whereEqualTo("empId", empId) // Filter by empId
                .get()
                .await()

            for (document in querySnapshot) {
                val leave = document.toObject(Leave::class.java)
                Log.d("LeaveFetch", "Leave fetched: $leave")
                leaveList.add(leave)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error fetching leaves for employee: ${e.message}")
        }

        return leaveList
    }
}
