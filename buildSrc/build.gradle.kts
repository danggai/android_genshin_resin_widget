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