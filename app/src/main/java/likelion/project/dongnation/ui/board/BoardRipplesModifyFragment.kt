package likelion.project.dongnation.ui.board

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardRipplesModifyBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.model.TipsRipple
import likelion.project.dongnation.ui.main.MainActivity

class BoardRipplesModifyFragment : Fragment() {

    lateinit var fragmentBoardRipplesModifyBinding: FragmentBoardRipplesModifyBinding
    lateinit var mainActivity: MainActivity

    lateinit var ripple: TipsRipple
    var tipIdx = ""

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardRipplesModifyBinding =
            FragmentBoardRipplesModifyBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        ripple = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("ripple", TipsRipple::class.java)!!
        } else {
            arguments?.getParcelable("ripple")!!
        }

        tipIdx = arguments?.getString("tipIdx").toString()

        fragmentBoardRipplesModifyBinding.run {

            initView()
            ripplesModify()

            materialToolbarBoardRipplesModify.run {
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_RIPPLES_MODIFY_FRAGMENT)
                }
            }

        }

        return fragmentBoardRipplesModifyBinding.root
    }

    fun initView() {
        fragmentBoardRipplesModifyBinding.apply {
            editTextBoardRipplesModify.text =
                Editable.Factory.getInstance().newEditable(ripple.rippleContent)
        }
    }

    fun ripplesModify() {
        fragmentBoardRipplesModifyBinding.buttonBoardRipplesModify.setOnClickListener {
            hideKeyboard()
            val newContent = fragmentBoardRipplesModifyBinding.editTextBoardRipplesModify.text.toString()
            val rippleId = ripple.rippleIdx

            if (newContent.isBlank()) {
                Snackbar.make(requireView(), "내용을 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            }
            else {
                val query = db.collection("tips")
                    .whereEqualTo("tipIdx", tipIdx)

                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val tipRipples = document["tipRipples"] as List<Map<String, Any>>?

                            if (tipRipples != null && tipRipples.isNotEmpty()) {
                                for (ripple in tipRipples) {
                                    val rippleIdx = ripple["rippleIdx"] as String?

                                    if (rippleIdx == rippleId) {

                                        val updatedRipple = ripple.toMutableMap()
                                        updatedRipple["rippleContent"] = newContent

                                        val updatedTipRipples = tipRipples.toMutableList()
                                        val index = updatedTipRipples.indexOf(ripple)
                                        if (index != -1) {
                                            updatedTipRipples[index] = updatedRipple
                                        }

                                        val tipRef = db.collection("tips").document(document.id)
                                        tipRef.update("tipRipples", updatedTipRipples)
                                            .addOnSuccessListener {
                                                mainActivity.removeFragment(MainActivity.BOARD_RIPPLES_MODIFY_FRAGMENT)
                                                Snackbar.make(requireView(), "댓글이 수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.e("BoardRipplesModifyFragment", "Error updating ripple content", exception)
                                            }
                                    }
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("BoardRipplesModifyFragment", "Error fetching documents", exception)
                    }
            }
        }

    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}