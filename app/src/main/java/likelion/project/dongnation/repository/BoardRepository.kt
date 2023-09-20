package likelion.project.dongnation.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.model.TipsRipple

class BoardRepository {

    private val db = Firebase.firestore

    suspend fun getAllBoard() : MutableList<Tips> {
        val querySnapshot = db.collection("tips")
            .orderBy("tipDate", Query.Direction.DESCENDING)
            .get()
            .await()

        val boardList = mutableListOf<Tips>()

        for (document in querySnapshot) {
            val tips = document.toObject(Tips::class.java)
            tips.tipIdx = document.id // 문서 ID를 donationIdx 필드에 할당
            boardList.add(tips)
        }

        return boardList
    }

    suspend fun deleteBoard(board: Tips) {
        db.collection("tips")
            .document(board.tipIdx)
            .delete()
            .await()
    }

    suspend fun getRipplesForBoard(tipIdx: String): MutableList<TipsRipple> {
        val ripples = mutableListOf<TipsRipple>()

        val querySnapshot = db.collection("tips")
            .whereEqualTo("tipIdx", tipIdx)
            .get()
            .await()

        for (document in querySnapshot) {
            val tipData = document.toObject(Tips::class.java)

            // tipData에서 tipRipples 필드를 가져옵니다.
            val tipRipples = tipData.tipRipples

            val sortedRipples = tipRipples.sortedByDescending { it.rippleDate }

            ripples.addAll(sortedRipples)
        }

        return ripples
    }

    suspend fun deleteRipples(tipIdx: String, rippleIdx: String) {

        val querySnapshot = db.collection("tips")
            .whereEqualTo("tipIdx", tipIdx)
            .get()
            .await()

        for (document in querySnapshot) {
            val tipRipples = document["tipRipples"] as List<Map<String, Any>>?

            if (tipRipples != null && tipRipples.isNotEmpty()) {
                val updatedRipples = tipRipples.filter { it["rippleIdx"] != rippleIdx }

                val tipRef = db.collection("tips").document(document.id)
                tipRef.update("tipRipples", updatedRipples)
                    .await()

            }
        }
    }

}