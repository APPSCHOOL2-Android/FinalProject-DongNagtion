package likelion.project.dongnation.repository

import android.content.Context
import likelion.project.dongnation.db.local.DataStoreDataSource

class OnboardingRepository(val context: Context) {
    private val dataStoreDataSource = DataStoreDataSource(context)

    suspend fun readOnboarding(key: String = ONBOARDING_KEY): String? {
        return dataStoreDataSource.readDataSource(key)
    }

    suspend fun writeOnboarding(key: String = ONBOARDING_KEY, value: String) {
        return dataStoreDataSource.writeDataSource(key, value)
    }

    companion object {
        private val ONBOARDING_KEY = "AFTER_SPLASH"
    }
}