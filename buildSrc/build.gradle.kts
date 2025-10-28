plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
    implementation("com.android.tools.build:gradle:8.4.2")
    implementation("com.android.tools.build:gradle-api:8.4.2")
    implementation("com.squareup:javapoet:1.13.0")
}