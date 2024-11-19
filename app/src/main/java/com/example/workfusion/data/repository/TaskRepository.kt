package com.example.workfusion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
)  {
    suspend fun uploadTask(){

    }
}