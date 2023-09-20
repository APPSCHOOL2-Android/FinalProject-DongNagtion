package likelion.project.dongnation.ui.board

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentBoardContentsBinding
import likelion.project.dongnation.databinding.ItemBoardContentsCommentBinding
import likelion.project.dongnation.databinding.ItemBoardContentsDialogBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.model.TipsRipple
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity
import java.util.Date
import kotlin.random.Random

class BoardContentsFragment : Fragment() {

    lateinit var fragmentBoardContentsBinding: FragmentBoardContentsBinding
    lateinit var mainActivity: MainActivity

    lateinit var board: Tips
    val userId = LoginViewModel.loginUserInfo.userId

    val db = Firebase.firestore
    val userName = LoginViewModel.loginUserInfo.userName

    lateinit var viewModel: BoardViewModel

    val tipsRippleDataList = mutableListOf<TipsRipple>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardContentsBinding = FragmentBoardContentsBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]

        board = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("board", Tips::class.java)!!
        } else {
            arguments?.getParcelable("board")!!
        }

        viewModel.run {
            ripplesLiveData.observe(viewLifecycleOwner) { ripplesList ->
                tipsRippleDataList.clear()
                ripplesList.forEach{ tipsRippleDataList.add(it) }
                fragmentBoardContentsBinding.recyclerViewBoardContents.adapter?.notifyDataSetChanged()
            }

            loadRipples(board.tipIdx)
        }

        fragmentBoardContentsBinding.run {
            // 바텀 네비게이션 안 보이게 하기
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.GONE

            materialToolbarBoardContents.run {
                title = board.tipTitle
                setNavigationIcon(R.drawable.ic_back_24dp)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.BOARD_CONTENTS_FRAGMENT)
                }
            }

            textViewBoardContentsWriter.text = board.tipWriterName
            textViewBoardContentsDate.text = mainActivity.formatTimeDifference(board.tipDate.toDate())
            textViewBoardContentsContents.text = board.tipContent

            viewPagerBoardContents.run {
                if (getImageList().isNotEmpty()) {
                    viewPagerBoardContents.adapter = BoardContentsViewPagerAdapter(getImageList())
                    viewPagerBoardContents.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    springDotsIndicatorBoardContents.setViewPager2(viewPagerBoardContents)    // indicator 설정
                }else {
                    layoutBoardContentsImg.visibility = View.GONE
                }

            }

            recyclerViewBoardContents.run {
                adapter = BoardContentsAdapter()
                layoutManager = LinearLayoutManager(context)
            }

            if (userId == board.tipWriterId) {
                // 게시글 수정 이미지 클릭
                imageViewBoardContentsEdit.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putParcelable("board", board)
                    mainActivity.replaceFragment(MainActivity.BOARD_MODIFY_FRAGMENT, true, bundle)
                }

                // 게시글 삭제 이미지 클릭
                imageViewBoardContentsDelete.setOnClickListener{
                    val binding = ItemBoardContentsDialogBinding.inflate(LayoutInflater.from(context))
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setView(binding.root)
                    val dialog = builder.create()

                    binding.buttonBoardContentsCancel.setOnClickListener {
                        dialog.dismiss()
                    }

                    binding.buttonBoardContentsDelete.setOnClickListener {
                        
                        viewModel.deleteBoard(board)

                        mainActivity.removeFragment(MainActivity.BOARD_CONTENTS_FRAGMENT)
                        Snackbar.make(requireView(), "게시글이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    dialog.show()
                }

            } else {
                imageViewBoardContentsEdit.visibility = View.GONE
                imageViewBoardContentsDelete.visibility = View.GONE
            }

            // 댓글 작성
            editTextBoardContentsComment.run {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        // 텍스트 변경 이전의 상태
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        // 텍스트가 변경될 때 호출됨
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // 텍스트 변경 후의 상태

                        if (s?.isNotEmpty() == true) {
                            imageButtonBoardContentsCommentButton.visibility = View.VISIBLE
                        } else {
                            imageButtonBoardContentsCommentButton.visibility = View.GONE
                        }
                    }
                })
            }

            // 댓글 작성 버튼 클릭
            imageButtonBoardContentsCommentButton.setOnClickListener {

                val rippleWriterId = userId
                val rippleWriterName = userName

                val rippleDate = Timestamp(Date())
                val rippleContent = editTextBoardContentsComment.text.toString()

                val rippleData = hashMapOf<String, Any>(
                    "tipIdx" to board.tipIdx,
                    "rippleIdx" to generateRandomRippleIdx(),
                    "rippleWriterId" to rippleWriterId,
                    "rippleWriterName" to rippleWriterName,
                    "rippleDate" to rippleDate,
                    "rippleContent" to rippleContent
                )

                val currentBoardRef = db.collection("tips").document(board.tipIdx)
                currentBoardRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val currentRipples = documentSnapshot.get("tipRipples") as? ArrayList<HashMap<String, Any>> ?: ArrayList()

                            currentRipples.add(rippleData)

                            currentBoardRef.update("tipRipples", currentRipples)
                                .addOnSuccessListener {
                                    viewModel.loadRipples(board.tipIdx)
                                    editTextBoardContentsComment.text.clear()
                                    hideKeyboard()
                                }
                                .addOnFailureListener { exception ->

                                }

                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("aaaaa", "게시물 문서 가져오기 실패: $exception")
                    }

            }

        }

        return fragmentBoardContentsBinding.root
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getImageList(): ArrayList<String> {

        val imageList = arrayListOf<String>()

        if (board.tipsImg.isNotEmpty()) {
            imageList.addAll(board.tipsImg)
        }

        return imageList
    }

    // 랜덤한 "rippleIdx" 값을 생성
    private fun generateRandomRippleIdx(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..10)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    inner class BoardContentsAdapter : RecyclerView.Adapter<BoardContentsAdapter.BoardContentsHolder>() {

        inner class BoardContentsHolder(binding: ItemBoardContentsCommentBinding) : RecyclerView.ViewHolder(binding.root){
            val imageViewCommentItemProfile : ImageView
            val textViewCommentItemWriter : TextView
            val imageViewCommentItemEdit : ImageView
            val imageViewCommentItemDelete : ImageView
            val textViewCommentItemContents : TextView
            val textViewCommentItemDate : TextView

            init {
                imageViewCommentItemProfile = binding.imageViewCommentItemProfile
                textViewCommentItemWriter = binding.textViewCommentItemWriter
                imageViewCommentItemEdit = binding.imageViewCommentItemEdit
                imageViewCommentItemDelete = binding.imageViewCommentItemDelete
                textViewCommentItemContents = binding.textViewCommentItemContents
                textViewCommentItemDate = binding.textViewCommentItemDate

                // 댓글 수정
                imageViewCommentItemEdit.setOnClickListener {
                    val position = bindingAdapterPosition
                    val selectedRipple = tipsRippleDataList[position]

                    var bundle = Bundle()
                    bundle.putParcelable("ripple", selectedRipple)
                    bundle.putString("tipIdx", board.tipIdx)
                    mainActivity.replaceFragment(MainActivity.BOARD_RIPPLES_MODIFY_FRAGMENT, true, bundle)
                }

                // 댓글 삭제
                imageViewCommentItemDelete.setOnClickListener {
                    val binding = ItemBoardContentsDialogBinding.inflate(LayoutInflater.from(context))
                    val builder = MaterialAlertDialogBuilder(mainActivity)
                    builder.setView(binding.root)
                    val dialog = builder.create()

                    binding.textViewBoardContentsTitle.text = "댓글을 삭제하시겠습니까?"

                    binding.buttonBoardContentsCancel.setOnClickListener {
                        dialog.dismiss()
                    }

                    binding.buttonBoardContentsDelete.setOnClickListener {

                        viewModel.deleteRipples(board.tipIdx, tipsRippleDataList[adapterPosition].rippleIdx)

                        Snackbar.make(requireView(), "댓글이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    dialog.show()
                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardContentsHolder {
            val binding = ItemBoardContentsCommentBinding.inflate(layoutInflater)
            val boardContentsHolder = BoardContentsHolder(binding)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return boardContentsHolder
        }

        override fun getItemCount(): Int {
            return tipsRippleDataList.size
        }

        override fun onBindViewHolder(holder: BoardContentsHolder, position: Int) {

            val tipTimestamp = tipsRippleDataList[position].rippleDate
            val formattedDate = mainActivity.formatTimeDifference(tipTimestamp.toDate())

            holder.textViewCommentItemWriter.text = tipsRippleDataList[position].rippleWriterName
            holder.textViewCommentItemContents.text = tipsRippleDataList[position].rippleContent
            holder.textViewCommentItemDate.text = formattedDate

            if (userId == tipsRippleDataList[position].rippleWriterId) {
                holder.imageViewCommentItemEdit.visibility = View.VISIBLE
                holder.imageViewCommentItemDelete.visibility = View.VISIBLE
            } else {
                holder.imageViewCommentItemEdit.visibility = View.GONE
                holder.imageViewCommentItemDelete.visibility = View.GONE
            }

        }

    }

}