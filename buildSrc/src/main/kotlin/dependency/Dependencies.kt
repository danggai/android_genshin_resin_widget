package dependency

object Dependencies {
    object Android {
        const val gradle = "com.android.tools.build:gradle:${Versions.buildGradleVersion}"
        const val material = "com.google.android.material:material:${Versions.materialVersion}"
        const val ads = "com.google.android.gms:play-services-ads:${Versions.googleAdsVersion}"
    }
    
    object AndroidX {
        const val core = "androidx.core:core-ktx:${Versions.coreVersion}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
        const val activity = "androidx.activity:activity-ktx:${Versions.activityKtxVersion}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragmentKtxVersion}"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintVersion}"
    }

    object Lifecycle {
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleVersion}"
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleVersion}"
    }
    
    object Java {
        const val java = "javax.inject:javax.inject:1"
    }

    object Kotlin {
        const val gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinGradleVersion}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinGradleVersion}"
    }
    
    object Google {
        const val services = "com.google.gms:google-services:${Versions.googleServiceVersion}"
        const val gson = "com.google.code.gson:gson:${Versions.gsonVersion}"
    }

    object Rx {
        const val android = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroidVersion}"
        const val java = "io.reactivex.rxjava2:rxjava:${Versions.rxJavaVersion}"
        const val kotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxKotlinVersion}"
    }

    object Coroutine {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutineVersion}"
    }

    object Glide {
        const val gilde = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
        const val compiler = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"
    }
    
    object DaggerHilt {
        const val gradle = "com.google.dagger:hilt-android-gradle-plugin:${Versions.daggerHiltVersion}"
        const val android = "com.google.dagger:hilt-android:${Versions.daggerHiltVersion}"
        const val compiler = "com.google.dagger:hilt-android-compiler:${Versions.daggerHiltVersion}"
    }

    object Hilt {
        const val work = "androidx.hilt:hilt-work:${Versions.hiltVersion}"
        const val compiler = "androidx.hilt:hilt-compiler:${Versions.hiltVersion}"
    }

    object Retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
        const val rxjava2Adapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofitVersion}"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpVersion}"
    }

    object Sandwich {
        const val sandwich = "com.github.skydoves:sandwich:${Versions.sandwichVersion}"
    }

    object Room {
        const val runtime = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val compiler = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val ktx = "androidx.room:room-ktx:${Versions.roomVersion}"
    }

    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:${Versions.firebaseVersion}"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val crashlyticsKtx = "com.google.firebase:firebase-crashlytics-ktx"
        const val crashlyticsGradle = "com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradleVersion}"
    }

    object Work {
        const val runtime = "androidx.work:work-runtime-ktx:${Versions.workerVersion}"
    }

    object Junit {
        const val junit = "junit:junit:${Versions.junitVersion}"
        const val ext = "androidx.test.ext:junit:${Versions.junitExtVersion}"
        const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoVersion}"
    }

    const val javapoet = "com.squareup:javapoet:${Versions.javapoetVersion}"
}