package likelion.project.dongnation.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import likelion.project.dongnation.repository.OnboardingRepository

class MainViewModel(context: Context): ViewModel() {
    private val onboardingRepository = OnboardingRepository(context)

    private val _isFirstVisitor = MutableStateFlow(false)
    val isFistVisitor = _isFirstVisitor.asStateFlow()

    init {
        checkFirstVisitor()
    }

    fun checkFirstVisitor() {
        viewModelScope.launch {
            val response = onboardingRepository.readOnboarding()
            _isFirstVisitor.update {
                response == null
            }
        }
    }
}