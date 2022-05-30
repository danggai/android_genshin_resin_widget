import dependency.Versions

plugins {
    id ("com.android.library")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("com.google.gms.google-services")
}

apply ("$rootDir/dependencies.gradle")

android {
    compileSdkVersion(Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.compileSdk)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
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
//            applicationIdSuffix ".debug"
            isDebuggable = true
            buildConfigField("long", "VERSION_CODE", "${.versionCode}")
            buildConfigField("String","VERSION_NAME","\"${.versionName}\"")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation project(":domain")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinGradleVersion}")

    // gson
    implementation ("com.google.code.gson:gson:${Versions.gsonVersion}")

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

    // OkHttp3 & Retrofit - for network
    implementation ("com.squareup.okhttp3:logging-interceptor:${Versions.okhttpVersion}")
    implementation ("com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}")
    implementation ("com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofitVersion}")

    // Sandwich - network success/fail Handling
    implementation ("com.github.skydoves:sandwich:${Versions.sandwichVersion}")

    // firebase
    implementation platform("com.google.firebase:firebase-bom:${Versions.firebaseVersion}")

    // unittest
    testImplementation ("junit:junit:${Versions.junitVersion}")
    androidTestImplementation ("androidx.test.ext:junit:${Versions.junitExtVersion}")
    androidTestImplementation ("androidx.test.espresso:espresso-core:${Versions.EspressoVersion}")
}

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}