package likelion.project.dongnation.ui.login

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import likelion.project.dongnation.BuildConfig
import java.util.Properties

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "${BuildConfig.KAKAO_NATIVE_APP_KEY}")
    }
}