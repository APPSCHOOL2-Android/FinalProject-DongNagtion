package likelion.project.dongnation.db.remote

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.model.Review
import likelion.project.dongnation.model.User

class ReviewDataSource {
    private val db = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    suspend fun addReview(review: Review): Flow<Result<Boolean>> {
        return flow {
            kotlin.runCatching {
                val reviewIdx = db.collection("Reviews").document().id
                val uploadedImages = mutableListOf<String>()
                review.reviewImg.forEach {
                    var file = Uri.parse(it)
                    val uploadTask = storageRef.child("reviewImages/${reviewIdx}/${file.lastPathSegment}.png")
                        .putFile(file)
                        .await()
                    val downloadUrl = uploadTask.storage.downloadUrl.await()
                    uploadedImages.add(downloadUrl.toString())
                }
                val updatedProduct = review.copy(reviewIdx = reviewIdx ,reviewImg = uploadedImages)
                db.collection("Reviews")
                    .document(reviewIdx)
                    .set(updatedProduct)
                    .await()
            }.onSuccess {
                emit(Result.success(true))
            }.onFailure {
                emit(Result.success(false))
            }
        }
    }

    suspend fun getReviews(donationIdx: String): Flow<Result<List<Review>>> {
        return flow {
            kotlin.runCatching {
                db.collection("Reviews")
                    .whereEqualTo("donationBoardId", donationIdx)
                    .get()
                    .await()
                    .toObjects(Review::class.java)
            }.onSuccess {
                emit(Result.success(it))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    suspend fun deleteReview(user: User) = withContext(Dispatchers.IO){
        db.collection("Reviews")
            .whereEqualTo("reviewWriter", user.userId)
            .get()
            .addOnSuccessListener {
                if(it.documents.size != 0){
                    for(document in it.documents){
                        db.document(document.reference.path).delete()
                    }
                }
            }
    }
}