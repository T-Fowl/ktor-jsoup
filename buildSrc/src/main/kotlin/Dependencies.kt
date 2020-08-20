object Versions {
    const val kotlin = "1.4.0"
    const val dokka = "0.9.18"
    const val bintray = "1.8.4"
    const val kosogor = "1.0.4"
}

@Suppress("ALL")
object Libraries {
    const val junit = "junit:junit:4.12"
    const val jsoup = "org.jsoup:jsoup:1.13.1"

    val ktor = Ktor

    object Ktor {
        const val version = "1.4.0"
        private const val prefix = "io.ktor:ktor"

        const val client = "$prefix-client:$version"
        const val clientMockJvm = "$prefix-client-mock-jvm:$version"
    }
}