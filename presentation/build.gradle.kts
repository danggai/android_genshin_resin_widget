import dependency.Versions

plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

apply ("$rootDir/dependencies.gradle")

android {
    compileSdkVersion(Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.compileSdk)
        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles = "consumer-rules.pro"
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("long", "VERSION_CODE", "${.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${.versionName}\"")
        }
        getByName("debug") {
            isDebuggable = true
            buildConfigField("long", "VERSION_CODE", "${.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${.versionName}\"")
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
    implementation project(":domain")

    implementation ("androidx.core:core-ktx:${Versions.coreVersion}")
    implementation ("androidx.appcompat:appcompat:${Versions.appCompatVersion}")
    implementation ("androidx.activity:activity-ktx:${Versions.activityKtxVersion}")
    implementation ("androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}")
    implementation ("com.google.android.material:material:${Versions.materialVersion}")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinGradleVersion}")

    // gson
    implementation ("com.google.code.gson:gson:${Versions.gsonVersion}")

    // layout
    implementation ("com.google.android.material:material:${Versions.materialVersion}")
    implementation ("androidx.constraintlayout:constraintlayout:${Versions.constraintVersion}")

    // lifecycle
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}")

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
    implementation  ("androidx.hilt:hilt-work:${Versions.hiltWorkVersion}")
    kapt            ("androidx.hilt:hilt-compiler:${Versions.hiltWorkVersion}")

    // gilde - image
    implementation ("com.github.bumptech.glide:glide:${Versions.glideVersion}")
    annotationProcessor ("com.github.bumptech.glide:compiler:${Versions.glideVersion}")

    // google ad
    implementation ("com.google.android.gms:play-services-ads:${Versions.googleAdsVersion}")

    // firebase crashlytics
    implementation ("com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalyticsVersion}")
    implementation ("com.google.firebase:firebase-crashlytics-ktx:${Versions.firebaseCrashlyticsVersion}")

    // worker - Background Action
    implementation ("androidx.work:work-runtime-ktx:${Versions.workerVersion}")

    // unittest
    testImplementation ("junit:junit:${Versions.junitVersion}")
    androidTestImplementation ("androidx.test.ext:junit:${Versions.junitExtVersion}")
    androidTestImplementation ("androidx.test.espresso:espresso-core:${Versions.EspressoVersion}")
}