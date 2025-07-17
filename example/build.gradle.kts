plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.okhttp)
    implementation(project(":")) // com.tfowl.ktor:ktor-jsoup:x.y.z
}
