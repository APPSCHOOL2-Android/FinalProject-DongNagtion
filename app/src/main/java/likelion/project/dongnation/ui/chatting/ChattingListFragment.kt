package likelion.project.dongnation.ui.chatting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentChattingListBinding
import likelion.project.dongnation.databinding.ItemChattingListRowBinding
import likelion.project.dongnation.ui.main.MainActivity

class ChattingListFragment : Fragment() {
    private lateinit var fragmentChattingListBinding: FragmentChattingListBinding
    private lateinit var mainActivity: MainActivity

    var list = arrayListOf<Int>(1, 2, 3, 4, 5, 6)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChattingListBinding = FragmentChattingListBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        initUI()

        return fragmentChattingListBinding.root
    }

    private fun initUI(){
        fragmentChattingListBinding.run{
            toolbarChattingList.run{
                setTitle(R.string.chatting_list_title)
            }

            recyclerViewChattingList.run{
                adapter = RecyclerAdapter()
                layoutManager = LinearLayoutManager(mainActivity)
            }
        }
    }

    inner class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
        inner class ViewHolder(itemChattingListRowBinding: ItemChattingListRowBinding)
            : RecyclerView.ViewHolder(itemChattingListRowBinding.root), OnClickListener {

            var textViewMessage: TextView
            var textViewContent: TextView
            var textViewType: TextView
            var textViewDate: TextView


            init {
                textViewMessage = itemChattingListRowBinding.textViewItemChattingListMessage
                textViewContent = itemChattingListRowBinding.textViewItemChattingListContent
                textViewType = itemChattingListRowBinding.textViewItemChattingListType
                textViewDate = itemChattingListRowBinding.textViewItemChattingListDate
            }

            override fun onClick(p0: View?) {
            }
        }

        // ViewHolder 반환
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemChattingListRowBinding = ItemChattingListRowBinding.inflate(layoutInflater)
            val viewHolder = ViewHolder(itemChattingListRowBinding)

            // 클릭 이벤트 설정
            itemChattingListRowBinding.root.setOnClickListener(viewHolder)

            // 가로 세로 길이 설정
            val params = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            itemChattingListRowBinding.root.layoutParams = params

            return viewHolder
        }

        // 전체 행 개수 반환
        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        }
    }
}