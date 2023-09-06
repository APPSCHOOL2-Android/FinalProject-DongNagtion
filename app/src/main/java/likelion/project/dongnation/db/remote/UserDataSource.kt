package likelion.project.dongnation.db.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.dongnation.model.User

class UserDataSource {
    private val db = Firebase.firestore

    suspend fun getAllUsers(): MutableList<User> {
        val querySnapshot = db.collection("users").get().await()
        return querySnapshot.toObjects(User::class.java)
    }
}