object Versions {
    const val CORE_VERSION = "1.8.0"
    const val APPCOMPAT_VERSION = "1.6.1"
    const val CONSTRAINT_LAYOUT_VERSION = "2.1.4"
    const val SPLASH_SCREEN_VERSION = "1.0.1"
    const val MATERIAL_VERSION = "1.9.0"
    const val FIREBASE_BOM_VERSION = "32.2.2"
    const val FIREBASE_STORAGE_VERSION = "20.2.1"
    const val FIREBASE_PLAY_VERSION = "20.6.0"
    const val LOTTIE_VERSION = "5.2.0"
    const val SKELETON_VERSION = "0.5.0"
    const val JUNIT_VERSION = "4.13.2"
    const val JUNIT_EXT_VERSION = "1.1.5"
    const val ESPRESSO_VERSION = "3.5.1"
    const val VIEWMODEL_VERSION = "2.6.1"
    const val DATASTORE_VERSION = "1.0.0"
    const val GLIDE_VERSION = "4.15.1"
    const val KAKAO_VERSION = "2.15.0"
    const val NAVER_VERSION = "5.7.0"
    const val NAVERMAP_VERSION = "3.17.0"
    const val LOCATION_VERSION = "21.0.1"
    const val ROOM_VERSION = "2.5.2"
    const val LIFECYCLE_SCOPE_VERSION = "2.4.0"
    const val RETROFIT_VERSION = "2.9.0"
    const val Indicator_VERSION = "5.0"
}

object AndroidXDeps {
    const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_VERSION}"
    const val APPCOMPAT =  "androidx.appcompat:appcompat:${Versions.APPCOMPAT_VERSION}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT_VERSION}"
    const val SPLASH_SCREEN = "androidx.core:core-splashscreen:${Versions.SPLASH_SCREEN_VERSION}"
    const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEWMODEL_VERSION}"
    const val LIFECYCLE_SCOPE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE_SCOPE_VERSION}"
}

object GoogleDeps {
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL_VERSION}"
}

object FirebaseDeps {
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM_VERSION}"
    const val FIREBASE_ANALYTICS_KTX = "com.google.firebase:firebase-analytics-ktx"
    const val FIREBASE_DATABASE = "com.google.firebase:firebase-firestore-ktx"
    const val FIREBASE_STORAGE = "com.google.firebase:firebase-storage:${Versions.FIREBASE_STORAGE_VERSION}"
    const val FIREBASE_AUTH = "com.google.firebase:firebase-auth-ktx"
    const val FIREBASE_PLAY = "com.google.android.gms:play-services-auth:${Versions.FIREBASE_PLAY_VERSION}"
}

object LottieDeps {
    const val LOTTIE = "com.airbnb.android:lottie:${Versions.LOTTIE_VERSION}"
}

object SkeletonDeps {
    const val SHIMMER = "com.facebook.shimmer:shimmer:${Versions.SKELETON_VERSION}"
}

object TestDeps {
    const val JUNIT = "junit:junit:${Versions.JUNIT_VERSION}"
    const val JUNIT_EXT = "androidx.test.ext:junit:${Versions.JUNIT_EXT_VERSION}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_VERSION}"
}

object DataStore {
    const val PREFERENCES = "androidx.datastore:datastore-preferences:${Versions.DATASTORE_VERSION}"
    const val PREFERENCES_CORE =  "androidx.datastore:datastore-preferences-core:${Versions.DATASTORE_VERSION}"
}

object GlideDeps {
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE_VERSION}"
}
object KakaoDeps {
    const val KAKAO = "com.kakao.sdk:v2-all:${Versions.KAKAO_VERSION}" // 전체 모듈 설치, 2.11.0 버전부터 지원
}

object NaverDeps {
    const val NAVER = "com.navercorp.nid:oauth-jdk8:${Versions.NAVER_VERSION}"
}

object MapDeps {
    const val NAVERMAP = "com.naver.maps:map-sdk:${Versions.NAVERMAP_VERSION}"
    const val LOCATION = "com.google.android.gms:play-services-location:${Versions.LOCATION_VERSION}"
}

object Room {
    const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM_VERSION}"
    const val ROOM_CORUTINE = "androidx.room:room-ktx:${Versions.ROOM_VERSION}"
    const val ROOM_KAPT = "androidx.room:room-compiler:${Versions.ROOM_VERSION}"
    const val KAPT_COMPILE =  "androidx.room:room-compiler:${Versions.ROOM_VERSION}"
}

object RetrofitDeps {
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT_VERSION}"
    const val CONVERTER_GSON =  "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT_VERSION}"
}

object IndicatorDeps {
    const val Indicator = "com.tbuonomo:dotsindicator:${Versions.Indicator_VERSION}"
}