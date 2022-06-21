import dependency.Versions
import dependency.Dependencies

plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "danggai.app.resinwidget"
        minSdk = Versions.minSdk
        targetSdk = Versions.compileSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${Versions.versionName}\"")
        }
        getByName("debug") {
//            applicationIdSuffix ".debug"
            isDebuggable = true
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
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation (project(":data"))
    implementation (project(":domain"))
    implementation (project(":presentation"))

    // coroutine
    implementation (Dependencies.Coroutine.core)
    implementation (Dependencies.Coroutine.android)

    // rx
    implementation (Dependencies.Rx.android)
    implementation (Dependencies.Rx.java)
    implementation (Dependencies.Rx.kotlin)

    // hilt - DI
    implementation (Dependencies.DaggerHilt.android)
    implementation (Dependencies.Hilt.work)
    kapt (Dependencies.DaggerHilt.compiler)
    kapt (Dependencies.Hilt.compiler)

    // OkHttp3 & Retrofit - for network
    implementation (Dependencies.Retrofit.loggingInterceptor)
    implementation (Dependencies.Retrofit.gsonConverter)
    implementation (Dependencies.Retrofit.retrofit)
    implementation (Dependencies.Retrofit.rxjava2Adapter)

    // Sandwich - network success/fail Handling
    implementation (Dependencies.Sandwich.sandwich)

    // firebase crashlytics
    implementation (Dependencies.Firebase.crashlyticsKtx)
}