// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    apply ("$rootDir/dependencies.gradle")

    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
//        classpath ("com.android.tools.build:gradle:${Versions.buildGradleVersion}")
//        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinGradleVersion")
//        classpath ("com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion")
//        classpath ("com.google.gms:google-services:${Versions.googleServiceVersion")
//        classpath ("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradleVersion")
        classpath ("com.android.tools.build:gradle:7.1.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.41")
        classpath ("com.google.gms:google-services:4.3.10")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}