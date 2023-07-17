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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation("com.android.tools.build:gradle-api:7.4.2")
    implementation("com.squareup:javapoet:1.13.0")
}