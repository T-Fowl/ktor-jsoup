plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.ktor:ktor-client-okhttp:1.6.0")
    implementation("com.tfowl.ktor:ktor-jsoup:1.5.4")
}