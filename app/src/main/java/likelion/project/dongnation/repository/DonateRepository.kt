package likelion.project.dongnation.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.model.Donations
import java.util.UUID

class DonateRepository {
    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    suspend fun getAllDonate(): Flow<Result<List<Donations>>> {
        return flow {
            kotlin.runCatching {
                db.collection("Donations")
                    .orderBy("donationTimeStamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Donations::class.java)
            }.onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun getOneDonate(idx: String): Donations {
        Log.d("Donatsions66", "${idx}")
        val querySnapshot = db.collection("Donations")
            .whereEqualTo("donationIdx", idx)
            .get()
            .await()
            .toObjects(Donations::class.java)

        return querySnapshot.find { it.donationIdx == idx } ?: Donations()
    }

    fun addDonate(donate: Donations): Task<Void> {
        db.collection("Donations")
            .add(donate)
            .addOnSuccessListener { documentReference ->
                val newDonateId = documentReference.id
                db.collection("Donations")
                    .document(newDonateId)
                    .update("donationIdx", newDonateId)
            }

            .addOnFailureListener { e ->
                // 데이터 추가 실패
            }

        return Tasks.forResult(null)
    }

    suspend fun modifyDonate(idx: String, newData: Donations): Task<Void> {
        val documentReference = db.collection("Donations").document(idx)
        withContext(Dispatchers.IO) {
            documentReference.update(
                "donationType", newData.donationType,
                "donationCategory", newData.donationCategory,
                "donationTitle", newData.donationTitle,
                "donationSubtitle", newData.donationSubtitle,
                "donationContent", newData.donationContent,
                "donationImg", newData.donationImg
            ).await()
        }

        return Tasks.forResult(null)
    }

    fun uploadImage(uri: Uri): Task<Uri> {
        val imageRef = storageRef.child("donateImg/${UUID.randomUUID()}.jpg")
        return imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }
    }

    fun deleteImage(fileUrl: String) {
        val imageRef = storageRef.child(fileUrl)
        imageRef.delete()
    }
}