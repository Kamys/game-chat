plugins {
    kotlin("jvm") version "1.9.10"
}


allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("org.jetbrains.kotlin.jvm")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
    }
}
