package likelion.project.dongnation.api

import com.google.gson.GsonBuilder
import likelion.project.dongnation.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


object NaverMapClient {
    private const val BASE_URL = "https://naveropenapi.apigw.ntruss.com"
    val apiService: NaverMapService by lazy { instance.create(NaverMapService::class.java) }

    private val instance: Retrofit
        get() {
            val gson = GsonBuilder().setLenient().create()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(provideOkHttpClient(AppInterceptor()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain) = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NAVER_MAP_CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NAVER_MAP_CLIENT_SECRET)
                .build()
            proceed(newRequest)
        }
    }
}