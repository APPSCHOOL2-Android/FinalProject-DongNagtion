package likelion.project.dongnation.ui.board

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Tips
import likelion.project.dongnation.repository.BoardRepository

class BoardViewModel : ViewModel() {

    val boardRepository = BoardRepository()
    val boardLiveData = MutableLiveData<MutableList<Tips>>()

    fun loadBoard() {
        viewModelScope.launch {
            val board = boardRepository.getAllBoard()
            boardLiveData.postValue(board)
        }
    }

}