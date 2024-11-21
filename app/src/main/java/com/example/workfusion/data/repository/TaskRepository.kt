package com.example.workfusion.data.repository

import android.util.Log
import com.example.workfusion.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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



    suspend fun fetchAllTasks(): List<Task> {
        val organizationId=auth.currentUser?.uid?: throw Exception("Unable to Fetch task")
        val db = FirebaseFirestore.getInstance()
        val taskList = mutableListOf<Task>()

        try {
            // Get all documents in the "tasks" collection
            val querySnapshot: QuerySnapshot = db.collection("organizations")
                .document(organizationId)
                .collection("tasks")
                .get()
                .await()

            // Convert each document to Task
            for (document in querySnapshot.documents) {
                val task = document.toObject(Task::class.java)
                if (task != null) {
                    taskList.add(task)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return taskList
    }

    suspend fun fetchTasksForEmployee(organizationId: String, empId: String): List<Task> {
        val db = FirebaseFirestore.getInstance()
        val taskList = mutableListOf<Task>()

        try {
            // Query documents where empId matches
            val querySnapshot: QuerySnapshot = db.collection("organizations")
                .document(organizationId)
                .collection("tasks")
                .whereEqualTo("empId", empId)
                .get()
                .await()

            // Convert each document to Task
            for (document in querySnapshot.documents) {
                val task = document.toObject(Task::class.java)
                if (task != null) {
                    taskList.add(task)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return taskList
    }



}