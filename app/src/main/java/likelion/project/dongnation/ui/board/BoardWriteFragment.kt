package likelion.project.dongnation.ui.board

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardWriteBinding
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.util.Date


class BoardWriteFragment : Fragment() {

    lateinit var fragmentBoadWriteBinding: FragmentBoardWriteBinding
    lateinit var mainActivity: MainActivity

    val db = Firebase.firestore
    val userId = LoginViewModel.loginUserInfo.userId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoadWriteBinding = FragmentBoardWriteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoadWriteBinding.run {

            materialToolbarBoardWrite.run {

                // 바텀 네비게이션 안 보이게 하기
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

                materialToolbarBoardWrite.run {

                    setNavigationOnClickListener {
                        mainActivity.removeFragment(MainActivity.BOARD_WRITE_FRAGMENT)
                    }

                }

            }

            // 작성하기 버튼
            buttonBoardWrite.setOnClickListener {
                val tipsCollection = db.collection("tips")

                val tipWriterId = userId
                Log.d("aaaaa", "tipWriterId : ${tipWriterId}")
                val tipTitle = editTextBoardWriteTitle.text.toString()
                val tipContent = editTextBoardWrite.text.toString()

                // 현재 시간을 얻어와서 Timestamp로 변환
                val tipDate = Timestamp(Date())

                // 데이터를 매핑해서 Firestore 문서로 추가
                val newTip = hashMapOf(
                    "tipWriterId" to tipWriterId,
                    "tipTitle" to tipTitle,
                    "tipContent" to tipContent,
                    "tipDate" to tipDate
                )

                tipsCollection.add(newTip)
                    .addOnSuccessListener { documentReference ->

                        // 추가한 데이터의 문서 ID를 가져온다
                        val newTipId = documentReference.id

                        // 문서 ID를 사용하여 tipIdx 필드를 업데이트한다
                        tipsCollection.document(newTipId)
                            .update("tipIdx", newTipId)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener { e ->
                                Log.d("BoardWriteFragment", "Error updating tipIdx", e)

                            }

                        mainActivity.removeFragment(MainActivity.BOARD_WRITE_FRAGMENT)
                    }
                    .addOnFailureListener { e ->
                        Log.d("BoardWriteFragment", "Error adding document", e)

                    }
            }

        }

        return fragmentBoadWriteBinding.root
    }

}