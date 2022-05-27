plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
}
android {
    apply from: "$rootDir/dependencies.gradle"

    compileSdkVersion(versions.compileSdk)

    defaultConfig {
        applicationId = "danggai.app.resinwidget"
        minSdkVersion(versions.minSdk)
        targetSdkVersion(versions.compileSdk)
        versionCode = versions.versionCode
        versionName = versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${defaultConfig.versionName}\"")
        }
        getByName("debug") {
//            applicationIdSuffix ".debug"
            isDebuggable = true
            buildConfigField("long", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${defaultConfig.versionName}\"")
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
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$versions.coroutineVersion")

    // rx
    implementation ("io.reactivex.rxjava2:rxandroid:$versions.rxAndroidVersion")
    implementation ("io.reactivex.rxjava2:rxjava:$versions.rxJavaVersion")
    implementation ("io.reactivex.rxjava2:rxkotlin:$versions.rxKotlinVersion")

    // hilt - DI
    implementation  ("com.google.dagger:hilt-android:$versions.hiltVersion")
    kapt            ("com.google.dagger:hilt-android-compiler:$versions.hiltVersion")
    implementation  ("androidx.hilt:hilt-work:$versions.hiltWorkVersion")
    kapt            ("androidx.hilt:hilt-compiler:$versions.hiltWorkVersion")

    // OkHttp3 & Retrofit - for network
    implementation ("com.squareup.okhttp3:logging-interceptor:$versions.okhttpVersion")
    implementation ("com.squareup.retrofit2:converter-gson:$versions.retrofitVersion")
    implementation ("com.squareup.retrofit2:retrofit:$versions.retrofitVersion")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:$versions.retrofitVersion")

    // Sandwich - network success/fail Handling
    implementation ("com.github.skydoves:sandwich:$versions.sandwichVersion")

    // firebase crashlytics
    implementation ("com.google.firebase:firebase-crashlytics-ktx:$versions.firebaseCrashlyticsVersion")

}