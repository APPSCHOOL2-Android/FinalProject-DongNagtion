package likelion.project.dongnation.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.dongnation.model.Donations

class DonateRepository {
    private val db = Firebase.firestore

    suspend fun getAllDonate(): MutableList<Donations> {
        val querySnapshot = db.collection("Donations")
            .orderBy("donationTimeStamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val donationsList = mutableListOf<Donations>()

        for (document in querySnapshot) {
            val donations = document.toObject(Donations::class.java)
            donationsList.add(donations)
        }

        return donationsList
    }

    suspend fun getOneDonate(idx : String): Donations {
        val querySnapshot = db.collection("Donations")
            .whereEqualTo("donationIdx", idx)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.toObject(Donations::class.java)!!
    }

    suspend fun addDonate(donate : Donations){
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
    }

    suspend fun modifyDonate(donations: Donations){

    }
}