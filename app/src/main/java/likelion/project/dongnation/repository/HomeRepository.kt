package likelion.project.dongnation.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.dongnation.model.Donations

class HomeRepository {
    private val db = Firebase.firestore

    suspend fun getAllDonate(): MutableList<Donations> {
        val querySnapshot = db.collection("Donations")
            .orderBy("donationTimeStamp", Query.Direction.DESCENDING)
            .get()
            .await()

        val donationsList = mutableListOf<Donations>()

        for (document in querySnapshot) {
            val donations = document.toObject(Donations::class.java)
            donations.donationIdx = document.id // 문서 ID를 donationIdx 필드에 할당
            donationsList.add(donations)
        }

        return donationsList
    }
}