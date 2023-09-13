package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
                    mainActivity.replaceFragment(MainActivity.BOARD_CONTENTS_FRAGMENT, true, null)
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
            val currentDate = Date() // 현재 날짜 및 시간

            val timeDifference = currentDate.time - tipTimestamp.toDate().time
            val timeDifferenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference)
            val timeDifferenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference)
            val timeDifferenceInHours = TimeUnit.MILLISECONDS.toHours(timeDifference)
            val timeDifferenceInDays = TimeUnit.MILLISECONDS.toDays(timeDifference)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val formattedDate = when {
                timeDifferenceInSeconds < 60 -> { // 60초 미만: "1분 전"과 같이 표시
                    "${timeDifferenceInSeconds}초 전"
                }
                timeDifferenceInMinutes < 60 -> { // 60분 미만: "1시간 전"과 같이 표시
                    "${timeDifferenceInMinutes}분 전"
                }
                timeDifferenceInHours < 24 -> { // 24시간 미만: "1시간 전"과 같이 표시
                    "${timeDifferenceInHours}시간 전"
                }
                timeDifferenceInDays <= 30 -> { // 30일 이내: "5일 전"과 같이 표시
                    "${timeDifferenceInDays}일 전"
                }
                else -> { // 30일 이후: 년도-월-일 형식으로 표시
                    dateFormat.format(tipTimestamp.toDate())
                }
            }

            holder.textViewTitle.text = tipsDataList[position].tipTitle
            holder.textViewWriter.text = tipsDataList[position].tipWriterName
            holder.textViewDate.text = formattedDate

            if (tipsDataList[position].tipsImg.isNotEmpty()) {
                val imageUrl = tipsDataList[position].tipsImg[0] // 첫 번째 이미지 URL을 가져옴
                Glide.with(holder.imageViewUser.context)
                    .load(imageUrl)
                    .into(holder.imageViewUser)
            } else {
                holder.imageViewUser.setImageDrawable(null)
            }

        }

    }

}