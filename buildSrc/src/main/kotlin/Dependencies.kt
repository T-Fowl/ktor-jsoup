object Versions {
    const val kotlin = "1.3.31"
}

@Suppress("ALL")
object Libraries {
    const val junit = "junit:junit:4.12"
    const val mockk = "io.mockk:mockk:1.9.3"
    const val jsoup = "org.jsoup:jsoup:1.11.3"
    val ktor = Ktor
    val kodein = Kodein
    val kotlin = Kotlin
    val kotlinx = Kotlinx

    object Kotlin {
        const val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    }

    object Ktor {
        private const val version = "1.2.0"
        private const val prefix = "io.ktor:ktor"

        const val client = "$prefix-client:$version"
        const val clientCore = "$prefix-client-core:$version"
        const val clientCoreJvm = "$prefix-client-core-jvm:$version"
        const val clientAuthBasic = "$prefix-client-auth-basic:$version"
        const val clientWebsocket = "$prefix-client-websocket:$version"
        const val clientLoggingJvm = "$prefix-client-logging-jvm:$version"

        const val clientOkHttp = "$prefix-client-okhttp:$version"
        const val clientApache = "$prefix-client-apache:$version"
        const val clientCio = "$prefix-client-cio:$version"
        const val clientJetty = "$prefix-client-jetty:$version"

        const val clientJson = "$prefix-client-json:$version"
        const val clientJsonJvm = "$prefix-client-json-jvm:$version"

        const val jackson = "$prefix-jackson:$version"
        const val clientJackson = "$prefix-client-jackson:$version"
        const val gson = "$prefix-gson:$version"
        const val clientGson = "$prefix-client-gson:$version"

        const val velocity = "$prefix-velocity:$version"
        const val freemarker = "$prefix-freemarker:$version"

        const val features = "$prefix-features:$version"
        const val auth = "$prefix-auth:$version"
        const val authJwt = "$prefix-auth-jwt:$version"
        const val authLdap = "$prefix-auth-ldap:$version"
        const val htmlBuilder = "$prefix-html-builder:$version"
        const val locations = "$prefix-locations:$version"
        const val metrics = "$prefix-metrics:$version"
        const val websockets = "$prefix-websockets:$version"
        const val network = "$prefix-network:$version"
        const val networkTls = "$prefix-network-tls:$version"
        const val webjars = "$prefix-webjars:$version"


        const val server = "$prefix-server:$version"
        const val serverCore = "$prefix-server-core:$version"
        const val serverNetty = "$prefix-server-netty:$version"
        const val serverJetty = "$prefix-server-jetty:$version"
        const val serverCio = "$prefix-server-cio:$version"
        const val serverTomcat = "$prefix-server-tomcat:$version"
        const val serverSessions = "$prefix-server-sessions:$version"
        const val serverHostCommon = "$prefix-server-host-common:$version"

        const val serverTests = "$prefix-server-tests:$version"
        const val clientMock = "$prefix-client-mock:$version"
        const val clientMockJvm = "$prefix-client-mock-jvm:$version"
    }

    object Kotlinx {

        const val serializationRuntime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.10.0"

        val coroutines = Coroutines

        object Coroutines {
            private const val version = "1.2.1"
            private const val prefix = "org.jetbrains.kotlinx:kotlinx-coroutines"

            const val core = "$prefix-core:$version"
            const val jdk8 = "$prefix-jdk8:$version"
            const val javafx = "$prefix-javafx:$version"
        }
    }

    object Kodein {
        const val genericJvm = "org.kodein.di:kodein-di-generic-jvm:6.2.0"
    }
}