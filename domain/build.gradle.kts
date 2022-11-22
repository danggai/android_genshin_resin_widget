import dependency.Versions

plugins {
    id ("java-library")
    id ("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation (dependency.Dependencies.Kotlin.stdlib)

    implementation (dependency.Dependencies.Coroutine.core)

    implementation(platform(dependency.Dependencies.Firebase.bom))

    implementation (dependency.Dependencies.Java.java)
}