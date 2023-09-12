package likelion.project.dongnation.ui.board

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.databinding.FragmentBoardMainBinding
import likelion.project.dongnation.databinding.ItemBoardMainBinding
import likelion.project.dongnation.ui.main.MainActivity

class BoardMainFragment : Fragment() {

    lateinit var fragmentBoardMainBinding: FragmentBoardMainBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBoardMainBinding = FragmentBoardMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentBoardMainBinding.run {

            searchViewBoarMainSearch.run {

                // 바텀 네비게이션 보이게하기
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE

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
            return 20
        }

        override fun onBindViewHolder(holder: BoarMainHolder, position: Int) {
            holder.textViewTitle.text = "꿀팁 제목입니다"
        }

    }

}