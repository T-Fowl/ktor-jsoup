import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version Versions.kotlin
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = Repositories.Kotlin.kotlinx)
}

dependencies {
    compile(Libraries.kotlin.stdlibJdk8)
    
    with(Libraries.ktor) {
        compile(client)
    }

    compile(Libraries.jsoup)

    testCompile(Libraries.junit)
    testCompile(Libraries.mockk)
}

