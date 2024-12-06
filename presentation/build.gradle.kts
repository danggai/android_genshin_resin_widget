import dependency.Versions

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${Versions.versionName}\"")
        }
        getByName("debug") {
            buildConfigField("long", "VERSION_CODE", "${Versions.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${Versions.versionName}\"")
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
    implementation(project(":domain"))

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.android.material)
    implementation(libs.android.ads)

    // Gson
    implementation(libs.google.gson)

    // Lifecycle
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)

    // Coroutine
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Rx
    implementation(libs.rx.android)
    implementation(libs.rx.java)
    implementation(libs.rx.kotlin)

    // Hilt - DI
    implementation(libs.hilt.work)
    implementation(libs.daggerHilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.daggerHilt.compiler)

    // Glide - image
    // kapt(libs.glide.gilde)
    // kapt(libs.glide.compiler)

    // Firebase Crashlytics
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    // Worker - Background Action
    implementation(libs.work.runtime)

    // TedPermission - Permission Check
    implementation(libs.tedPermission.normal)
    implementation(libs.tedPermission.coroutine)

    // Unit Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}