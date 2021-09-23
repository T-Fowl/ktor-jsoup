package com.tfowl.ktor.client.features

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.cio.*
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

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
                    "https://example.org/text/html" to Resource("sample.html", ContentType.Text.Html),
                    "https://example.org/text/xml" to Resource("sample.xml", ContentType.Text.Xml),
                    "https://example.org/application/xml" to Resource("sample.xml", ContentType.Application.Xml),
                    "https://example.org/application/rss" to Resource("sample.rss", ContentType.Application.Rss)
                )

                val resource = resources[request.url.fullUrl] ?: error("Unhandled ${request.url.fullUrl}")

                respond(
                    resource(resource.file).toByteReadChannel(),
                    headers = headersOf("Content-Type", listOf(resource.contentType.toString()))
                )
            }
        }
    }

    @Test
    fun `feature should parse html responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<Document>("https://example.org/text/html")
            assertEquals("Sample Document", document.body().text())
        }
    }

    @Test
    fun `feature should parse xml responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val textDocument = client.get<Document>("https://example.org/text/xml")
            assertEquals("Sample Document", textDocument.text())

            val applicationDocument = client.get<Document>("https://example.org/application/xml")
            assertEquals("Sample Document", applicationDocument.text())
        }
    }

    @Test
    fun `feature should not parse for non-document types`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<String>("https://example.org/text/html")
            assertTrue(document.contains("html"))
        }
    }

    @Test
    @Suppress("UNUSED_VARIABLE")
    fun `feature should not parse unregistered content types`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        assertThrows<NoTransformationFoundException> {
            runBlocking {
                val document = client.get<Document>("https://example.org/application/rss")
            }
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
            val document = client.get<Document>("https://example.org/application/rss")
            assertEquals("Sample RSS", document.select("rss>channel>title").text())
        }
    }
}