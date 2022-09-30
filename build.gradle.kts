import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
}
apply(plugin = "com.vanniktech.maven.publish")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    /*
        TODO: Use gradle catalog when dependabot supports it:
         https://github.com/dependabot/dependabot-core/issues/3471
         https://github.com/dependabot/dependabot-core/issues/3121
     */

    api("io.ktor:ktor-client:2.0.0")
    testImplementation("io.ktor:ktor-client-mock:2.1.2")

    api("org.jsoup:jsoup:1.14.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
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

plugins.withId("com.vanniktech.maven.publish") {
    configure<com.vanniktech.maven.publish.MavenPublishPluginExtension> {
        sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
    }
}