package com.example.workfusion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmployeeRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun getOrganizationId(): String{

        return auth.currentUser?.uid?: throw Exception("User not logged in.")
    }
    suspend fun getAllEmployeeDetails(organizationId: String): List<Map<String, Any>> {
        return try {
            val employeeQuery = db.collection("organizations")
                .document(organizationId)
                .collection("employees")
                .get()
                .await()

            if (employeeQuery.isEmpty) throw Exception("No employees found.")

            // Extract `empId` and `name` from each employee document
            employeeQuery.documents.map { doc ->
                mapOf(
                    "empId" to (doc.getLong("empId") ?: throw Exception("Employee ID missing.")),
                    "name" to (doc.getString("name") ?: throw Exception("Name missing."))
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch employees: ${e.message}")
        }
    }

}