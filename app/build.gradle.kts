import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "likelion.project.dongnation"
    compileSdk = 33

    val properties = Properties()
    properties.load(project.rootProject.file("local.properties").inputStream())
    val kakaoNativeAppKey = properties["KAKAO_NATIVE_APP_KEY"] ?: ""
    val naverOauthClientId = properties["NAVER_OAUTH_CLIENT_ID"] ?: ""
    val naverOauthClientSecret = properties["NAVER_OAUTH_CLIENT_SECRET"] ?: ""
    val naverOauthClientName = properties["NAVER_OAUTH_CLIENT_NAME"] ?: ""
    val naverMapClientId = properties["NAVER_MAP_CLIENT_ID"] ?: ""
    val naverMapClientSecret = properties["NAVER_MAP_CLIENT_SECRET"] ?: ""
    val googleOauthWebClientId = properties["GOOGLE_OAUTH_WEB_CLIENT_ID"] ?: ""

    defaultConfig {
        applicationId = "likelion.project.dongnation"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "$kakaoNativeAppKey")
        buildConfigField("String", "NAVER_OAUTH_CLIENT_ID", "$naverOauthClientId")
        buildConfigField("String", "NAVER_OAUTH_CLIENT_SECRET", "$naverOauthClientSecret")
        buildConfigField("String", "NAVER_OAUTH_CLIENT_NAME", "$naverOauthClientName")
        buildConfigField("String", "GOOGLE_OAUTH_WEB_CLIENT_ID", "$googleOauthWebClientId")
        buildConfigField("String", "NAVER_OAUTH_CLIENT_ID", "$naverOauthClientId")
        buildConfigField("String", "NAVER_MAP_CLIENT_ID", "$naverMapClientId")
        buildConfigField("String", "NAVER_MAP_CLIENT_SECRET", "$naverMapClientSecret")
        manifestPlaceholders["kakaoNativeAppKey"] = kakaoNativeAppKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    // androidx
    implementation(AndroidXDeps.CORE_KTX)
    implementation(AndroidXDeps.APPCOMPAT)
    implementation(AndroidXDeps.CONSTRAINT_LAYOUT)
    implementation(AndroidXDeps.VIEWMODEL)
    implementation(AndroidXDeps.SPLASH_SCREEN)
    implementation(AndroidXDeps.LIFECYCLE_SCOPE)
    implementation(AndroidXDeps.FRAGMENT)

    // Google
    implementation(GoogleDeps.MATERIAL)

    // Map
    implementation(MapDeps.NAVERMAP)
    implementation(MapDeps.LOCATION)
    implementation(MapDeps.OKHTTP)
    implementation(MapDeps.JSON)

    // Firebase
    implementation(platform(FirebaseDeps.FIREBASE_BOM))
    implementation(FirebaseDeps.FIREBASE_ANALYTICS_KTX)
    implementation(FirebaseDeps.FIREBASE_DATABASE)
    implementation(FirebaseDeps.FIREBASE_STORAGE)
    implementation(FirebaseDeps.FIREBASE_AUTH)
    implementation(FirebaseDeps.FIREBASE_PLAY)

    // Lottie
    implementation(LottieDeps.LOTTIE)

    // Skeleton
    implementation(SkeletonDeps.SHIMMER)

    // DataStore
    implementation(DataStore.PREFERENCES)
    implementation(DataStore.PREFERENCES_CORE)

    //Glide
    implementation(GlideDeps.GLIDE)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Test
    testImplementation(TestDeps.JUNIT)
    androidTestImplementation(TestDeps.JUNIT_EXT)
    androidTestImplementation(TestDeps.ESPRESSO_CORE)

    // Room
    implementation(Room.ROOM_CORUTINE)
    implementation(Room.ROOM_RUNTIME)
    kapt(Room.KAPT_COMPILE)

    // KaKao
    implementation(KakaoDeps.KAKAO)

    // Naver
    implementation(NaverDeps.NAVER)

    // Retrofit2
    implementation(RetrofitDeps.RETROFIT)
    implementation(RetrofitDeps.CONVERTER_GSON)

    // Indicator
    implementation(IndicatorDeps.Indicator)

    // TextViewMore
    implementation(TextViewMore.TEXTVIEW_MORE)
}