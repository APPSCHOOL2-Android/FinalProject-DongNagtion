package likelion.project.dongnation.repository

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import likelion.project.dongnation.model.Tips

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

}