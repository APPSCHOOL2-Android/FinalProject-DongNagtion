package likelion.project.dongnation.ui.board

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.model.TipsRipple
import likelion.project.dongnation.repository.BoardRepository

class BoardViewModel : ViewModel() {

    val boardRepository = BoardRepository()
    val boardLiveData = MutableLiveData<MutableList<Tips>>()
    val searchBoardLiveData = MutableLiveData<MutableList<Tips>>()
    val ripplesLiveData = MutableLiveData<MutableList<TipsRipple>>()

    fun loadBoard() {
        viewModelScope.launch {
            val board = boardRepository.getAllBoard()
            boardLiveData.postValue(board)
        }
    }

    fun searchBoard(word : String) {
        viewModelScope.launch {
            val search = boardRepository.getAllBoard()
            val searchList = mutableListOf<Tips>()

            for (s in search){
                if (s.tipTitle.contains(word) ||
                    s.tipContent.contains(word)) {

                    searchList.add(s)
                }
            }

            searchBoardLiveData.postValue(searchList)
        }
    }

    fun deleteBoard(board: Tips) {
        viewModelScope.launch {
            boardRepository.deleteBoard(board)
        }
    }

    fun loadRipples(tipIdx: String) {
        viewModelScope.launch {
            val ripples = boardRepository.getRipplesForBoard(tipIdx)
            ripplesLiveData.postValue(ripples)
        }
    }

}