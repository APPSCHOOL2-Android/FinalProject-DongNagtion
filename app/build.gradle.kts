plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")

}

android {
    namespace = "likelion.project.dongnation"
    compileSdk = 33

    defaultConfig {
        applicationId = "likelion.project.dongnation"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    // androidx
    implementation(AndroidXDeps.CORE_KTX)
    implementation(AndroidXDeps.APPCOMPAT)
    implementation(AndroidXDeps.CONSTRAINT_LAYOUT)
    implementation(AndroidXDeps.VIEWMODEL)
    implementation(AndroidXDeps.SPLASH_SCREEN)

    // Google
    implementation(GoogleDeps.MATERIAL)

    // Map
    implementation(MapDeps.NAVERMAP)
    implementation(MapDeps.LOCATION)

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
}