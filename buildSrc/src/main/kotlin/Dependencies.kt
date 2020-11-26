object Versions {
    const val kotlin = "1.4.10"
    const val dokka = "0.10.1"
    const val kosogor = "1.0.10"
}

@Suppress("ALL")
object Libraries {
    const val jsoup = "org.jsoup:jsoup:1.13.1"

    val ktor = Ktor
    val junit = JUnit

    object Ktor {
        const val version = "1.4.1"
        private const val prefix = "io.ktor:ktor"

        const val client = "$prefix-client:$version"
        const val clientMockJvm = "$prefix-client-mock-jvm:$version"
    }

    object JUnit {
        private const val version = "5.7.0"
        private const val prefix = "org.junit.jupiter:junit"

        const val jupiter = "$prefix-jupiter:$version"
    }
}