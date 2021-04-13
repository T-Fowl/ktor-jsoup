import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI
import java.net.URL

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
    id("org.jetbrains.dokka") version "1.4.30"
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

//tasks.withType<org.gradle.jvm.tasks.Jar>().configureEach {
//    manifest {
//        attributes
//    }
//}

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

tasks.create<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    archiveClassifier.set("sources")
}

tasks.create<Jar>("javadocJar") {
    from(tasks.dokkaJavadoc.get().outputs)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))

            pom {
                name.set(project.name)
                description.set("")
                url.set("")

                licenses {
                    license {
                        name.set("")
                        url.set("")
                    }
                }
                developers {
                    developer {
                        name.set("Thomas Fowler")
                    }
                }
                scm {
                    url.set("")
                    connection.set("")
                    developerConnection.set("")
                }
            }
        }
    }

    repositories {
        maven {
            name = "mavencentral"
            url = URI.create("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("SONATYPE_NEXUS_USERNAME")
                password = System.getenv("SONATYPE_NEXUS_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}