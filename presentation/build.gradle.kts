import dependency.Versions
import dependency.Dependencies

plugins {
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("com.android.library")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
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

    implementation (Dependencies.Kotlin.stdlib)

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
    implementation (Dependencies.Hilt.compiler)
    implementation (Dependencies.DaggerHilt.android)
    implementation (Dependencies.DaggerHilt.compiler)

    // gilde - image
    implementation (Dependencies.Glide.gilde)
    implementation (Dependencies.Glide.compiler)

    // firebase crashlytics
    implementation (Dependencies.Firebase.analytics)
    implementation (Dependencies.Firebase.crashlyticsKtx)

    // worker - Background Action
    implementation (Dependencies.Work.runtime)

    // unittest
    implementation (Dependencies.Junit.junit)
    implementation (Dependencies.Junit.ext)
    implementation (Dependencies.Junit.espressoCore)
}