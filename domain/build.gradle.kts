plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.java.inject)
    implementation(libs.google.gson)
}