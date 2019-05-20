package com.tfowl.ktor.client.features

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.*
import io.ktor.util.InternalAPI
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.io.jvm.javaio.toByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.io.core.ExperimentalIoApi
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.junit.Test
import java.io.InputStream


@ExperimentalIoApi
@InternalAPI
class JsoupFeatureTests {

    private data class Resource(val file: String, val contentType: ContentType)

    private fun resource(resource: String): InputStream =
            JsoupFeatureTests::class.java.getResourceAsStream(resource) ?: error("Missing testing file: $resource")

    private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort

    private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

    private val mockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val resources = mapOf(
                        "https://example.org/html" to Resource("sample.html", ContentType.Text.Html),
                        "https://example.org/xml" to Resource("sample.xml", ContentType.Text.Xml),
                        "https://example.org/rss" to Resource("sample.rss", ContentType.Application.Rss)
                )

                val resource = resources[request.url.fullUrl] ?: error("Unhandled ${request.url.fullUrl}")

                respond(resource(resource.file).toByteReadChannel(),
                        headers = headersOf("Content-Type", listOf(resource.contentType.toString())))
            }
        }
    }

    @Test
    fun `feature should parse html responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<Document>("https://example.org/html")
            assertEquals("Sample Document", document.body().text())
        }
    }

    @Test
    fun `feature should parse xml responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<Document>("https://example.org/xml")
            assertEquals("Sample Document", document.text())
        }
    }

    @Test
    fun `feature should not parse for non-document responses`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<String>("https://example.org/html")
            assertTrue(document.contains("html"))
        }
    }

    @Test(expected = NoTransformationFoundException::class)
    fun `feature should not parse unregistered content types`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<Document>("https://example.org/rss")
        }
    }

    @Test
    fun `feature should parse additional content types`() {
        val client = mockClient.config {
            install(JsoupFeature) {
                parsers[ContentType.Application.Rss] = Parser.xmlParser()
            }
        }

        runBlocking {
            val document = client.get<Document>("https://example.org/rss")
            assertEquals("Sample RSS", document.select("rss>channel>title").text())
        }
    }
}