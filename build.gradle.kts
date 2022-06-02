// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            setUrl("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }

    dependencies {
        classpath (dependency.Dependencies.Android.gradle)
        classpath (dependency.Dependencies.Kotlin.gradle)
        classpath (dependency.Dependencies.DaggerHilt.gradle)
        classpath (dependency.Dependencies.Google.services)
        classpath (dependency.Dependencies.Firebase.crashlyticsGradle)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven {
            setUrl("http://jcenter.bintray.com")
            isAllowInsecureProtocol = true
        }
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}