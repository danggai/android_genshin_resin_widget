// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    apply from: "$rootDir/dependencies.gradle"

    repositories {
        google()
        maven {
            url = uri("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        classpath ("com.android.tools.build:gradle:$Versions.buildGradleVersion")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$versions.kotlinGradleVersion")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:$versions.hiltVersion")
        classpath ("com.google.gms:google-services:$versions.googleServiceVersion")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:$versions.crashlyticsGradleVersion")
    }
}

allprojects {
    repositories {
        google()
        maven {
            url = uri("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}