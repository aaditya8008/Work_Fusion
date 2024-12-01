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
    val organizationId = auth.currentUser?.uid ?: throw Exception("Unable to Fetch leaves")
    suspend fun updateLeaveStatus(leaveId: Long, newStatus: String) {
        try {

            val querySnapshot = db.collection("leaves")
                .whereEqualTo("leaveId", leaveId)
                .whereEqualTo("organizationId",organizationId)
                .get()
                .await()

            // Check if the query returned any documents
            if (!querySnapshot.isEmpty) {

                val document = querySnapshot.documents[0]


                document.reference.update("status", newStatus).await()
                Log.d("Firestore", "Leave status updated successfully for leaveId: $leaveId")
            } else {
                throw Exception("No leave document found with leaveId: $leaveId")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating leave status: ${e.message}")
            throw Exception("Error updating leave status: ${e.message}")
        }
    }

    suspend fun uploadLeave(
        empId: Long,orgId:String, name: String, reason: String,
        startDate: String, endDate: String, subject: String, type: String
    ) {
        try {
            val organizationId = orgId
            val org = db.collection("organizations").document(organizationId)
            Log.d("LEAVE","Repo $empId $name $reason $startDate $endDate")
            // Get the current leave counter and increment it
            val documentSnapshot = org.get().await()
            if (documentSnapshot.exists()) {
                Log.d("LEAVE","Inside $empId $name $reason $startDate $endDate")
                val leaveCounter = documentSnapshot.getLong("leaveCounter") ?: 0L


                val leaveId = leaveCounter + 1


                org.update("leaveCounter", leaveCounter + 1).await()

                // Prepare leave data
                val leaveData = hashMapOf(
                    "leaveId" to leaveId,
                    "empId" to empId,
                    "name" to name,
                    "reason" to reason,
                    "subject" to subject,
                    "type" to type,
                    "status" to "Pending",
                    "startDate" to startDate,
                    "endDate" to endDate,
                    "organizationId" to organizationId
                )


                db.collection("leaves")
                    .document("$leaveId")
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

        val leaveList = mutableListOf<Leave>()

        try {
            val querySnapshot: QuerySnapshot = db
                .collection("leaves")
                .whereEqualTo("organizationId",organizationId)
                .get()
                .await()


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
                .whereEqualTo("empId", empId)
                .whereEqualTo("organizationId",organizationId)
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
