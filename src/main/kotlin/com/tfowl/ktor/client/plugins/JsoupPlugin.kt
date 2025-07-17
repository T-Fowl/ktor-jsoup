@file:Suppress("unused")

package com.tfowl.ktor.client.plugins

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

/**
 * [HttpClient] plugin that parses response bodies into Jsoup [Document]
 * class using a provided [Parser]
 *
 * By default,
 *
 * [ContentType.Text.Html] is parsed using [Parser.htmlParser].
 *
 * [ContentType.Text.Xml] & [ContentType.Application.Xml] are parsed using [Parser.xmlParser].
 *
 * Note: It will only parse registered content types and for receiving
 * [Document] or superclasses.
 */
val JsoupPlugin = createClientPlugin("JsoupPlugin", ::JsoupPluginConfig) {
    val parsers = pluginConfig.parsers

    transformResponseBody { response, channel, typeInfo ->
        if (!typeInfo.type.java.isAssignableFrom(Document::class.java))
            return@transformResponseBody null

        val responseContentType = response.contentType() ?: return@transformResponseBody null

        val parser = parsers.firstNotNullOfOrNull { (type, parser) ->
            parser.takeIf { responseContentType.match(type) }
        } ?: return@transformResponseBody null

        val bodyContent = channel.readRemaining().readText()
        val baseUri = response.request.url.toString()

        /* Jsoup Parsers internally contain a stateful TreeBuilder,
           We need to create a deep copy to avoid issues with
           concurrency */
        val document = Jsoup.parse(bodyContent, baseUri, parser.newInstance())

        return@transformResponseBody document
    }
}

/**
 * [JsoupPlugin] configuration that is used during installation
 */
class JsoupPluginConfig {

    internal val parsers = mutableMapOf(
        ContentType.Text.Html to Parser.htmlParser(),
        ContentType.Text.Xml to Parser.xmlParser(),
        ContentType.Application.Xml to Parser.xmlParser()
    )

    fun parseAsHtml(contentType: ContentType) {
        parsers[contentType] = Parser.htmlParser()
    }

    fun parseAsXml(contentType: ContentType) {
        parsers[contentType] = Parser.xmlParser()
    }

    fun parseAs(contentType: ContentType, parser: Parser) {
        parsers[contentType] = parser
    }
}

/**
 * Install [JsoupPlugin]
 */
@Suppress("FunctionName")
fun HttpClientConfig<*>.Jsoup(block: JsoupPluginConfig.() -> Unit = {}) {
    install(JsoupPlugin, block)
}
