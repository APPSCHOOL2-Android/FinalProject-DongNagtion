package likelion.project.dongnation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.repository.DonateRepository

class HomeViewModel : ViewModel() {
    private val donateRepository = DonateRepository()

    val donatesLiveData = MutableLiveData<MutableList<Donations>>()
    val searchLiveData = MutableLiveData<MutableList<Donations>>()

    fun loadDonations(){
        viewModelScope.launch {
            val donates = donateRepository.getAllDonate()
            donatesLiveData.postValue(donates)
        }
    }

    fun searchDonate(word : String) {
        viewModelScope.launch {
            val search = donateRepository.getAllDonate()
            val searchList = mutableListOf<Donations>()

            for (s in search){
                if (s.donationTitle.contains(word) ||
                    s.donationSubtitle.contains(word) ||
                    s.donationContent.contains(word)) {

                    searchList.add(s)
                }
            }

            searchLiveData.postValue(searchList)
        }
    }
}