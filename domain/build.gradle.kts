import dependency.Versions

plugins {
    id ("java-library")
    id ("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation (dependency.Dependencies.Kotlin.stdlib)

    implementation (dependency.Dependencies.Coroutine.core)

    implementation (dependency.Dependencies.Java.java)
}