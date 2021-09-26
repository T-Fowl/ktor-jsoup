package com.tfowl.ktor.client.features

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.runBlocking
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@ExperimentalIoApi
@Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
class JsoupFeatureTests {

    private val RESOURCES = mapOf(
        ContentType.Text.Html to "sample.html",
        ContentType.Text.Xml to "sample.xml",
        ContentType.Application.Xml to "sample.xml",
        ContentType.Application.Rss to "sample.rss"
    )

    private fun resourceAsByteReadChannel(resource: String): ByteReadChannel =
        JsoupFeatureTests::class.java.getResourceAsStream(resource)?.toByteReadChannel()
            ?: error("Missing testing file: $resource")

    private fun urlFor(type: ContentType): Url = URLBuilder("https://example.com/resource").apply {
        parameters.append("contentType", type)
    }.build()

    private fun Url.requestedContentType(): ContentType? =
        parameters["contentType"]?.let { ContentType.parse(it) }

    private fun MockRequestHandleScope.respond(resource: String, contentType: ContentType) =
        respond(resourceAsByteReadChannel(resource), headers = buildHeaders {
            append(HttpHeaders.ContentType, contentType)
        })

    private val mockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val contentType = request.url.requestedContentType()
                    ?: error("Could not determine requested content type: ${request.url}")

                val resourceFile = RESOURCES[contentType]
                    ?: error("No resource registered for $contentType")

                respond(resourceFile, contentType)
            }
        }
    }

    @Test
    fun `feature should parse html responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<Document>(urlFor(ContentType.Text.Html))
            assertEquals("Sample Document", document.body().text())
        }
    }

    @Test
    fun `feature should parse xml responses by default`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val textDocument = client.get<Document>(urlFor(ContentType.Text.Xml))
            assertEquals("Sample Document", textDocument.text())

            val applicationDocument = client.get<Document>(urlFor(ContentType.Application.Xml))
            assertEquals("Sample Document", applicationDocument.text())
        }
    }

    @Test
    fun `feature should not parse for non-document types`() {
        val client = mockClient.config {
            install(JsoupFeature)
        }

        runBlocking {
            val document = client.get<String>(urlFor(ContentType.Text.Html))
            assertTrue(document.contains("</html>"))
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
                val document = client.get<Document>(urlFor(ContentType.Application.Rss))
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
            val document = client.get<Document>(urlFor(ContentType.Application.Rss))
            assertEquals("Sample RSS", document.select("rss>channel>title").text())
        }
    }
}