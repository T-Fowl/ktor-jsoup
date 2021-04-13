import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tanvd.kosogor.proxy.publishJar
import java.net.URL

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("org.jetbrains.dokka") version "1.4.30"
    id("tanvd.kosogor") version "1.0.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
    jcenter() // Required for dokka
}

dependencies {
    /*
        TODO: Use gradle catalog when dependabot supports it:
         https://github.com/dependabot/dependabot-core/issues/3471
         https://github.com/dependabot/dependabot-core/issues/3121
     */

    api("io.ktor:ktor-client:1.4.1")
    testImplementation("io.ktor:ktor-client-mock-jvm:1.4.1")

    api("org.jsoup:jsoup:1.13.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

tasks.withType<Test>().all {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        externalDocumentationLink {
            url.set(URL("https://jsoup.org/apidocs/"))
            packageListUrl.set(URL("https://jsoup.org/apidocs/element-list"))
        }

        externalDocumentationLink {
            /* TODO: Use gradle catalog when dependabot adds support
                     https://github.com/dependabot/dependabot-core/issues/3471
                     https://github.com/dependabot/dependabot-core/issues/3121
            */
            val ktorVersion = configurations.api.get().dependencies.first { "io.ktor" == it.group }.version
            url.set(URL("https://api.ktor.io/$ktorVersion/"))
        }
    }
}

publishJar {

    publication {
        artifactId = "ktor-jsoup"
    }

    bintray {
        username = System.getenv("BINTRAY_USER")
        secretKey = System.getenv("BINTRAY_KEY")

        repository = "ktor"

        info {
            githubRepo = "https://github.com/T-Fowl/ktor-jsoup.git"
            vcsUrl = "https://github.com/T-Fowl/ktor-jsoup.git"
            license = "MIT"
        }
    }
}