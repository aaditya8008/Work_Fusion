package com.example.workfusion.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun uploadTask(
        empId: Long, name: String, description: String,
        startDate: String, endDate: String
    ) {
        try {
            val organizationId = auth.currentUser?.uid ?: throw Exception("Unable to get Id")
            val org = db.collection("organizations").document(organizationId)

            // Get the current employee counter and increment it
            val documentSnapshot = org.get().await()
            if (documentSnapshot.exists()) {
                val taskCounter = documentSnapshot.getLong("taskCounter") ?: 0L


                // Increment the counter and convert to Int safely
                val taskId = (taskCounter + 1).toLong()

                // Update the task counter in Firestore
                org.update("taskCounter", taskCounter + 1).await()

                // Prepare task data
                val taskData = hashMapOf(
                    "taskId" to taskId,
                    "empId" to empId,
                    "name" to name,
                    "description" to description,
                    "startDate" to startDate,
                    "endDate" to endDate
                )

                // Upload task to Firestore
                db.collection("organizations").document(organizationId)
                    .collection("tasks")
                    .document(empId.toString()+"."+name)
                    .collection("Tasks")
                    .document("Task: "+taskId)// Use taskId as document ID
                    .set(taskData)
                    .await()

                Log.d("Firestore", "Task successfully uploaded with taskId: $taskId")
            } else {
                throw Exception("Organization document does not exist")
            }
        } catch (e: Exception) {
            throw Exception("Task Upload Failed: ${e.message}")
        }
    }
}