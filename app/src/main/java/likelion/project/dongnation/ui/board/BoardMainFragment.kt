package likelion.project.dongnation.ui.board

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import likelion.project.dongnation.databinding.FragmentBoardMainBinding
import likelion.project.dongnation.databinding.ItemBoardMainBinding
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class BoardMainFragment : Fragment() {

    lateinit var fragmentBoardMainBinding: FragmentBoardMainBinding
    lateinit var mainActivity: MainActivity

    lateinit var viewModel: BoardViewModel
    val tipsDataList = mutableListOf<Tips>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardMainBinding = FragmentBoardMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]

        viewModel.run {
            boardLiveData.observe(viewLifecycleOwner) { tipsList ->
                tipsDataList.clear()
                tipsList.forEach{tipsDataList.add(it)}
                fragmentBoardMainBinding.recyclerBoardMainList.adapter?.notifyDataSetChanged()
            }

            loadBoard()
        }

        fragmentBoardMainBinding.run {

            // 바텀 네비게이션 보이게하기
            mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE

            searchViewBoarMainSearch.run {
                queryHint = "팁 게시글 검색"
                isSubmitButtonEnabled = true

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchBoardResult()
                        hideKeyboard()

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) {
                            loadAllBoards()
                        }
                        return true
                    }
                })
            }

            recyclerBoardMainList.run{
                adapter = BoardMainAdapter()
                layoutManager = LinearLayoutManager(context)
            }

            floatingActionButtonBoardMain.run {
                setOnClickListener {
                    mainActivity.replaceFragment(MainActivity.BOARD_WRITE_FRAGMENT,true, null)
                }
            }

        }

        return fragmentBoardMainBinding.root
    }

    private fun loadAllBoards() {
        viewModel.loadBoard()
        fragmentBoardMainBinding.layoutBardMainNoResult.visibility = View.GONE
    }
    private fun searchBoardResult(){
        val word = fragmentBoardMainBinding.searchViewBoarMainSearch.query.toString()

        viewModel.run {
            searchBoardLiveData.observe(viewLifecycleOwner){ searchList ->
                tipsDataList.clear()
                searchList.forEach{
                    tipsDataList.add(it)
                }

                if (tipsDataList.isNotEmpty()) {
                    fragmentBoardMainBinding.layoutBardMainNoResult.visibility = View.GONE
                }else {
                    fragmentBoardMainBinding.layoutBardMainNoResult.visibility = View.VISIBLE
                }

                fragmentBoardMainBinding.recyclerBoardMainList.adapter?.notifyDataSetChanged()
            }

            if (word.isNotEmpty()) {
                searchBoard(word)
                fragmentBoardMainBinding.layoutBardMainNoResult.visibility = View.GONE
            }else {
                fragmentBoardMainBinding.layoutBardMainNoResult.visibility = View.VISIBLE
            }

        }

    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    inner class BoardMainAdapter : RecyclerView.Adapter<BoardMainAdapter.BoarMainHolder>(){

        inner class BoarMainHolder(itemBoardMainBinding: ItemBoardMainBinding) : RecyclerView.ViewHolder(itemBoardMainBinding.root){
            val imageViewUser : ImageView
            val textViewTitle : TextView
            val textViewWriter : TextView
            val textViewDate : TextView

            init {
                imageViewUser = itemBoardMainBinding.imageViewUser
                textViewTitle = itemBoardMainBinding.textViewTitle
                textViewWriter = itemBoardMainBinding.textViewWriter
                textViewDate = itemBoardMainBinding.textViewDate

                // 게시판 클릭시
                itemBoardMainBinding.root.setOnClickListener {

                    val position = bindingAdapterPosition // 현재 아이템의 위치를 가져옴
                    if (position != RecyclerView.NO_POSITION) {
                        val selectedDonation = tipsDataList[position] // 선택한 아이템을 가져옴

                        var bundle = Bundle()
                        bundle.putParcelable("board", selectedDonation)
                        mainActivity.replaceFragment(MainActivity.BOARD_CONTENTS_FRAGMENT, true, bundle)
                        fragmentBoardMainBinding.searchViewBoarMainSearch.setQuery("", false)
                    }

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoarMainHolder {
            val itemBoardMainBinding = ItemBoardMainBinding.inflate(layoutInflater)
            val boarMainHolder = BoarMainHolder(itemBoardMainBinding)

            itemBoardMainBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return boarMainHolder
        }

        override fun getItemCount(): Int {
            return tipsDataList.size
        }

        override fun onBindViewHolder(holder: BoarMainHolder, position: Int) {
            val tipTimestamp = tipsDataList[position].tipDate // Firestore에서 가져온 Timestamp 객체
            val formattedDate = mainActivity.formatTimeDifference(tipTimestamp.toDate())

            holder.textViewTitle.text = tipsDataList[position].tipTitle
            holder.textViewWriter.text = tipsDataList[position].tipWriterName
            holder.textViewDate.text = formattedDate

            if (tipsDataList[position].tipsImg.isNotEmpty()) {
                val imageUrl = tipsDataList[position].tipsImg[0] // 첫 번째 이미지 URL을 가져옴
                Glide.with(holder.imageViewUser.context)
                    .load(imageUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(holder.imageViewUser)
            } else {
                holder.imageViewUser.setImageDrawable(null)
            }

        }

    }

}