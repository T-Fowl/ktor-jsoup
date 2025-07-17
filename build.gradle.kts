import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.publish)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}

repositories {
    mavenCentral()
}

dependencies {
    api(ktorLibs.client.core)
    testImplementation(ktorLibs.client.mock)

    api(libs.jsoup)
    testImplementation(libs.junit.jupiter)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        externalDocumentationLink(
            url = "https://jsoup.org/apidocs/",
            packageListUrl = "https://jsoup.org/apidocs/element-list"
        )

        externalDocumentationLink(
            url = "https://api.ktor.io/ktor-client/ktor-client-core",
            packageListUrl = "https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/package-list"
        )

        externalDocumentationLink(
            url = "https://api.ktor.io/ktor-http",
            packageListUrl = "https://api.ktor.io/ktor-http/ktor-http/package-list"
        )
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes("Automatic-Module-Name" to project.name)
    }
}