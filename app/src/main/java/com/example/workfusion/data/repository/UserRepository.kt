package com.example.workfusion.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    // Sign up organization
    suspend fun signupOrganization(organizationName: String, email: String, password: String,userType:String): Result<AuthResult> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val organization = hashMapOf(
                "organizationName" to organizationName,
                "email" to email,
                "userType" to userType,
                "employeeCounter" to 0L,
                "taskCounter" to 0L,// To track number of employees
                "leaveCounter" to 0L
            )
            db.collection("organizations").document(auth.currentUser!!.uid).set(organization).await()
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sign up employee
    suspend fun signupEmployee(
        organizationName: String, name: String, email: String,
        phoneNumber: String, password: String,
        userType:String
    ): Result<AuthResult> {
        return try {
            // Check if the organization exists
            val orgQuery = db.collection("organizations").whereEqualTo("organizationName", organizationName).get().await()
            if (orgQuery.isEmpty) throw Exception("Organization not found.")

            val organizationDoc = orgQuery.documents.first()
            val organizationId = organizationDoc.id

            // Get the current employee counter and increment it
            val employeeCounter = organizationDoc.getLong("employeeCounter") ?: 0L
            val newEmpId = (employeeCounter + 1).toInt()

            // Update the employeeCounter in the organization document
            db.collection("organizations").document(organizationId).update("employeeCounter", newEmpId).await()

            // Proceed with employee signup in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Create the employee document with the new empId
            val employee = hashMapOf(
                "empId" to newEmpId,
                "name" to name,
                "email" to email,
                "userType" to userType,
                "phoneNumber" to phoneNumber,
                "organizationId" to organizationId
            )

            // Save employee data under the organization
            db.collection("organizations").document(organizationId)
                .collection("employees").document(auth.currentUser!!.uid).set(employee).await()
            db.collection("employees").document(auth.currentUser!!.uid).set(employee).await()

            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login functionality
    suspend fun login(email: String, password: String): Result<AuthResult> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(authResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserType(userId: String): Result<String> {
        return try {
            // Check in the organizations collection
            val orgDoc = db.collection("organizations").document(userId).get().await()
            if (orgDoc.exists()) {
                return Result.success("organization")
            }

            // Check in the employees subcollection of all organizations
            val orgQuery = db.collection("organizations").get().await()
            for (doc in orgQuery.documents) {
                val empDoc = db.collection("organizations")
                    .document(doc.id)
                    .collection("employees")
                    .document(userId)
                    .get()
                    .await()
                if (empDoc.exists()) {
                    return Result.success("employee")
                }
            }

            Result.failure(Exception("User type not found."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}



