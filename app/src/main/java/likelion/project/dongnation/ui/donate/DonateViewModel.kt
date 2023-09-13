package likelion.project.dongnation.ui.donate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likelion.project.dongnation.model.Donations
import likelion.project.dongnation.repository.DonateRepository

class DonateViewModel : ViewModel() {
    private val donateRepository = DonateRepository()

    fun addDonate(donate : Donations){
        viewModelScope.launch(Dispatchers.IO) {
            donateRepository.addDonate(donate)
        }
    }
}