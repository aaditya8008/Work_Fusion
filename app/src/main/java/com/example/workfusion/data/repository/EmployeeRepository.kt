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
            val employeeQuery = db
                .collection("employees")
                .whereEqualTo("organizationId",organizationId)
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
    suspend fun getEmpDetail(Id: String): Map<String, Any> {
        return try {
            // Access the specific document by its ID
            val documentSnapshot = db
                .collection("employees")
                .document(Id)
                .get()
                .await()

            // Check if the document exists
            if (!documentSnapshot.exists()) throw Exception("Employee with ID $Id not found.")

            // Extract `empId` and `name` from the document
            val empId = documentSnapshot.getLong("empId") ?: throw Exception("Employee ID missing.")
            val name = documentSnapshot.getString("name") ?: throw Exception("Name missing.")
            val orgId = documentSnapshot.getString("organizationId") ?: throw Exception("organizationId missing.")

            // Return the data as a map
            mapOf(
                "empId" to empId,
                "name" to name,
                "orgId" to orgId
            )
        } catch (e: Exception) {
            throw Exception("Failed to fetch employee details: ${e.message}")
        }
    }



}