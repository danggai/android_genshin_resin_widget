import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
    mavenCentral()
    maven {
        setUrl("http://jcenter.bintray.com")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.20")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation("com.android.tools.build:gradle-api:7.2.1")
    implementation("com.squareup:javapoet:1.13.0")
}