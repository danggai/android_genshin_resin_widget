import dependency.Versions

plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
}
android {
    apply ("$rootDir/dependencies.gradle")

    compileSdkVersion(Versions.compileSdk)

    defaultConfig {
        applicationId = "danggai.app.resinwidget"
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.compileSdk)
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
}

dependencies {
    implementation project(":data")
    implementation project(":domain")
    implementation project(":presentation")

    // coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutineVersion}")

    // rx
    implementation ("io.reactivex.rxjava2:rxandroid:${Versions.rxAndroidVersion}")
    implementation ("io.reactivex.rxjava2:rxjava:${Versions.rxJavaVersion}")
    implementation ("io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlinVersion}")

    // hilt - DI
    implementation  ("com.google.dagger:hilt-android:${Versions.hiltVersion}")
    kapt            ("com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}")
    implementation  ("androidx.hilt:hilt-work:${Versions.hiltVersion}")
    kapt            ("androidx.hilt:hilt-compiler:${Versions.hiltVersion}")

    // OkHttp3 & Retrofit - for network
    implementation ("com.squareup.okhttp3:logging-interceptor:${Versions.okhttpVersion}")
    implementation ("com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}")
    implementation ("com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofitVersion}")

    // Sandwich - network success/fail Handling
    implementation ("com.github.skydoves:sandwich:${Versions.sandwichVersion}")

    // firebase crashlytics
    implementation ("com.google.firebase:firebase-crashlytics-ktx:${Versions.firebaseCrashlyticsVersion}")

}