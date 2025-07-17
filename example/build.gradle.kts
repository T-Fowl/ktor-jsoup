plugins {
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.ktor:ktor-client-okhttp:2.3.4")
    implementation(project(":")) // com.tfowl.ktor:ktor-jsoup:x.y.z
}
