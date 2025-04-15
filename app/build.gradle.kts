import dependency.Versions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = Versions.compileSdk
    compileSdkVersion = Versions.compileSdkString

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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${Versions.versionName}\"")
        }
        getByName("debug") {
//            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${Versions.versionName}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    namespace = "danggai.app.resinwidget"
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":presentation"))

// Coroutine
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Rx
    implementation(libs.rx.android)
    implementation(libs.rx.java)
    implementation(libs.rx.kotlin)
    
    // Glide - image
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Hilt - DI
    implementation(libs.daggerHilt.android)
    kapt(libs.daggerHilt.compiler)
    implementation(libs.hilt.work)
    kapt(libs.hilt.compiler)

    // OkHttp3 & Retrofit - for network
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.rxjava)

    // Sandwich - Network success/fail Handling
    implementation(libs.sandwich)

    // Firebase Crashlytics
    implementation(libs.firebase.crashlytics.ktx)
}