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
    val organizationId=auth.currentUser?.uid?: throw Exception("Unable to Fetch task")
    suspend fun uploadTask(
        empId: Long, name: String, description: String,
        startDate: String, endDate: String
    ) {
        try {
            val organizationId = auth.currentUser?.uid ?: throw Exception("Unable to get Id")
            val org = db.collection("organizations").document(organizationId)


            val documentSnapshot = org.get().await()
            if (documentSnapshot.exists()) {
                val taskCounter = documentSnapshot.getLong("taskCounter") ?: 0L

                val taskId = (taskCounter + 1).toLong()

                // Update the task counter in Firestore
                org.update("taskCounter", taskCounter + 1).await()

                val taskData = hashMapOf(
                    "taskId" to taskId,
                    "empId" to empId,
                    "name" to name,
                    "description" to description,
                    "status" to "Not Started",
                    "startDate" to startDate,
                    "endDate" to endDate,
                    "organizationId" to organizationId
                )


                db.collection("tasks")
                    .document("$taskId")
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

        val db = FirebaseFirestore.getInstance()
        val taskList = mutableListOf<Task>()

        try {

            val querySnapshot: QuerySnapshot = db

                .collection("tasks")
                .whereEqualTo("organizationId",organizationId)
                .get()
                .await()


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

    suspend fun fetchTasksForEmployee(): List<Task> {
        val empUid = auth.currentUser?.uid ?: throw Exception("Unable to Fetch task: No authenticated user")
        val db = FirebaseFirestore.getInstance()
        val taskList = mutableListOf<Task>()

        try {
            // Fetch organizationId for the employee
            val documentSnapshot = db.collection("employees")
                .document(empUid)
                .get()
                .await()

            val organizationId = documentSnapshot.getString("organizationId")
                ?: throw Exception("Organization ID not found for employee: $empUid")
            val empId = documentSnapshot.getLong("empId")
                ?: throw Exception("Employee ID not found for employee")


            Log.d("TaskFetch", "Tasks fetched for empId: $empId")

            val querySnapshot = db.collection("tasks")
                .whereEqualTo("empId", empId)
                .whereEqualTo("organizationId",organizationId)
                .get()
                .await()


            for (document in querySnapshot) {
                val task = document.toObject(Task::class.java)
                Log.d("TaskFetch", "Checking task with empId: ${task.empId}")

                Log.d("TaskFetch", "Task fetched: $task")
                taskList.add(task)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error fetching tasks for employee: ${e.message}")
        }

        return taskList
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: String) {
        try {
            val querySnapshot = db.collection("tasks")
                .whereEqualTo("taskId", taskId)
                .whereEqualTo("organizationId",organizationId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]

                document.reference.update("status", newStatus).await()
                Log.d("Firestore", "Task status updated successfully for taskId: $taskId")
            } else {
                throw Exception("No task document found with taskId: $taskId")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating task status: ${e.message}")
            throw Exception("Error updating task status: ${e.message}")
        }
    }



}