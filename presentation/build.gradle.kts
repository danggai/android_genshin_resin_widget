import dependency.Versions
import dependency.Dependencies

plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.compileSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${Versions.versionName}\"")
        }
        getByName("debug") {
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${Versions.versionName}\"")
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
        dataBinding = true
        viewBinding = true
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation (project(":domain"))

    implementation (Dependencies.AndroidX.core)
    implementation (Dependencies.AndroidX.appcompat)
    implementation (Dependencies.AndroidX.activity)
    implementation (Dependencies.AndroidX.fragment)
    implementation (Dependencies.AndroidX.constraintlayout)

    implementation (Dependencies.Android.material)
    implementation (Dependencies.Android.ads)

    // gson
    implementation (Dependencies.Google.gson)

    // lifecycle
    implementation (Dependencies.Lifecycle.runtime)
    implementation (Dependencies.Lifecycle.viewmodel)

    // coroutine
    implementation (Dependencies.Coroutine.core)
    implementation (Dependencies.Coroutine.android)

    // rx
    implementation (Dependencies.Rx.android)
    implementation (Dependencies.Rx.java)
    implementation (Dependencies.Rx.kotlin)

    // hilt - DI
    implementation (Dependencies.Hilt.work)
    implementation (Dependencies.DaggerHilt.android)
    kapt (Dependencies.Hilt.compiler)
    kapt (Dependencies.DaggerHilt.compiler)

    // gilde - image
//    kapt (Dependencies.Glide.gilde)
//    kapt (Dependencies.Glide.compiler)

    // firebase crashlytics
    implementation (Dependencies.Firebase.analytics)
    implementation (Dependencies.Firebase.crashlyticsKtx)

    // worker - Background Action
    implementation (Dependencies.Work.runtime)

    // unittest
    testImplementation (Dependencies.Junit.junit)
    androidTestImplementation (Dependencies.Junit.ext)
    androidTestImplementation (Dependencies.Junit.espressoCore)
}