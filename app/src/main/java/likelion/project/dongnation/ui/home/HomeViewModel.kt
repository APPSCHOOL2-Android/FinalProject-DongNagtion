package likelion.project.dongnation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.repository.HomeRepository

class HomeViewModel : ViewModel() {
    private val homeRepository = HomeRepository()

    val donatesLiveData = MutableLiveData<MutableList<Donations>>()

    fun loadDonations(){
        viewModelScope.launch {
            val donates = homeRepository.getAllDonate()
            donatesLiveData.postValue(donates)
        }
    }
}