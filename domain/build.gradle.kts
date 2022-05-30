import dependency.Versions

plugins {
    id ("java-library")
    id ("kotlin")
}

apply ("$rootDir/dependencies.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinGradleVersion}")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutineVersion}")

    implementation ("javax.inject:javax.inject:1")
}