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

    suspend fun fetchTasksForEmployee(): List<Task> {
        val empUid = auth.currentUser?.uid ?: throw Exception("Unable to Fetch task: No authenticated user")
        val db = FirebaseFirestore.getInstance()
        val taskList = mutableListOf<Task>()

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


// After fetching tasks
            Log.d("TaskFetch", "Tasks fetched for empId: $empId")

            val querySnapshot = db.collection("tasks")
                .whereEqualTo("empId", empId)  // This fetches only tasks with the specific empId
                .get()  // Get the query results
                .await()  // Wait for the query to complete asynchronously

            // Iterate through the querySnapshot
            for (document in querySnapshot) {
                val task = document.toObject(Task::class.java)
                Log.d("TaskFetch", "Checking task with empId: ${task.empId}")

                // No need for the extra check here, as Firestore is already filtered by empId
                Log.d("TaskFetch", "Task fetched: $task")
                taskList.add(task)  // Add the task to the list
            }

        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error fetching tasks for employee: ${e.message}")
        }

        return taskList
    }

    suspend fun updateTaskStatus(taskId: Long, newStatus: String) {
        try {
            // Query Firestore for the task document where taskId matches
            val querySnapshot = db.collection("tasks")
                .whereEqualTo("taskId", taskId)
                .get()
                .await()

            // Check if the query returned any documents
            if (!querySnapshot.isEmpty) {
                // Assuming taskId is unique, get the first document
                val document = querySnapshot.documents[0]

                // Update the status field in the matching document
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