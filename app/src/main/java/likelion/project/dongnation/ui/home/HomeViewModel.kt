package likelion.project.dongnation.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.repository.DonateRepository

class HomeViewModel : ViewModel() {
    private val donateRepository = DonateRepository()

    val donatesLiveData = MutableLiveData<MutableList<Donations>>()

    fun loadDonations(){
        viewModelScope.launch {
            val donates = donateRepository.getAllDonate()
            donatesLiveData.postValue(donates)
        }
    }
}