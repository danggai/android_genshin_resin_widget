import dependency.Versions
import dependency.Dependencies

plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("com.google.gms.google-services")
    id ("dagger.hilt.android.plugin")
}

android {
    compileSdk = Versions.compileSdk

    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.compileSdk
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
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation (project(":domain"))

    implementation (Dependencies.Kotlin.stdlib)

    // gson
    implementation (Dependencies.Google.gson)

    // coroutine
    implementation (Dependencies.Coroutine.core)
    implementation (Dependencies.Coroutine.android)

    // rx
    implementation (Dependencies.Rx.android)
    implementation (Dependencies.Rx.java)
    implementation (Dependencies.Rx.kotlin)

    // hilt - DI
    implementation (Dependencies.DaggerHilt.android)
    implementation (Dependencies.DaggerHilt.compiler)

    // OkHttp3 & Retrofit - for network
    implementation (Dependencies.Retrofit.loggingInterceptor)
    implementation (Dependencies.Retrofit.gsonConverter)
    implementation (Dependencies.Retrofit.retrofit)
    implementation (Dependencies.Retrofit.rxjava2Adapter)

    // Sandwich - network success/fail Handling
    implementation (Dependencies.Sandwich.sandwich)

    // firebase
    implementation (Dependencies.Firebase.bom)

    // unittest
    implementation (Dependencies.Junit.junit)
    implementation (Dependencies.Junit.ext)
    implementation (Dependencies.Junit.espressoCore)
}

android {
    compileOptions {
        sourceCompatibility (JavaVersion.VERSION_1_8 )
        targetCompatibility (JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}