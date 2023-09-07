package likelion.project.dongnation.db.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.model.User

class UserDataSource {
    private val db = Firebase.firestore

    suspend fun getAllUsers(): MutableList<User> {
        val querySnapshot = db.collection("users").get().await()
        return querySnapshot.toObjects(User::class.java)
    }

    suspend fun getUser(user: User): MutableList<User> {
        val querySnapshot = db.collection("users")
            .whereEqualTo("userId", user.userId)
            .get().await()
        return querySnapshot.toObjects(User::class.java)
    }

    suspend fun saveUser(user: User) = withContext(Dispatchers.IO){
        db.collection("users").add(user)
    }
}