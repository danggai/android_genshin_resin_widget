import dependency.Versions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    namespace = "danggai.app.resinwidget"
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
    ksp(libs.glide.compiler)

    // Hilt - DI
    implementation(libs.daggerHilt.android)
    ksp(libs.daggerHilt.compiler)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)

    // OkHttp3 & Retrofit - for network
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.rxjava)

    // Sandwich - Network success/fail Handling
    implementation(libs.sandwich)

    // Firebase Crashlytics

    // Firebase Crashlytics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
}