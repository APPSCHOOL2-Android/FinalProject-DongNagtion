package likelion.project.dongnation.db.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.model.User
import likelion.project.dongnation.ui.chatting.ChattingListViewModel
import likelion.project.dongnation.ui.chatting.ChattingViewModel

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
        db.collection("users")
            .document(user.userId)
            .set(user)
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

    suspend fun addUserExperience(id: String): Int = coroutineScope {
        val querySnapshot = db.collection("users")
            .whereEqualTo("userId", id)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val documentId = querySnapshot.documents[0].id
            val userExperience = querySnapshot.documents[0].get("userExperience") as Long + 1

            val documentReference = db.collection("users").document(documentId)
            val updates = hashMapOf("userExperience" to userExperience)

            documentReference.update(updates as Map<String, Any>).await()

            return@coroutineScope userExperience.toInt()
        } else {
            return@coroutineScope 0
        }
    }

    suspend fun getUserProfile(userId: String): String? {
        val querySnapshot = db.collection("users")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].getString("userProfile")
        } else {
            null
        }
    }

    suspend fun notifyUserChange() = withContext(Dispatchers.IO){
        db.collection("users")
            .addSnapshotListener { value, error ->
                ChattingViewModel.userChangeState.value = true
                ChattingListViewModel.receivingState.value = true
            }
    }
}