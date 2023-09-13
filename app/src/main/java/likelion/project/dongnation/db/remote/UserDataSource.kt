package likelion.project.dongnation.db.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    suspend fun updateAddress(user: User): Flow<Result<Boolean>> {
        return flow {
            runCatching {
                db.collection("users")
                    .document(user.userId)
                    .update("userAddress", user.userAddress)
                    .isComplete
            }.onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun updateTransferCode(user: User): Flow<Result<Boolean>> {
        return flow {
            runCatching {
                db.collection("users")
                    .document(user.userId)
                    .update("userTransCode", user.userTransCode)
                    .isComplete
            }.onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun getUserForId(id : String): User? {
        val querySnapshot = db.collection("users")
            .whereEqualTo("userId", id)
            .get().await()
        val users = querySnapshot.toObjects(User::class.java)

        return if (users.isNotEmpty()) users[0] else null
    }
}