@file:Suppress("unused")

package com.tfowl.ktor.client.features

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

/**
 * [HttpClient] feature that parses response bodies into Jsoup [Document]
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
 *
 * @property parsers Registered parsers for content types
 */
class JsoupFeature internal constructor(val parsers: Map<ContentType, Parser>) {

    /**
     * [JsoupFeature] configuration that is used during installation
     */
    class Config {

        /**
         * [Parsers][Parser] that will be used for each [ContentType]
         *
         * Defaults:
         *  - Html: [ContentType.Text.Html]
         *  - Xml: [ContentType.Text.Xml] and [ContentType.Application.Xml]
         */
        var parsers = mutableMapOf(
            ContentType.Text.Html to Parser.htmlParser(),
            ContentType.Text.Xml to Parser.xmlParser(),
            ContentType.Application.Xml to Parser.xmlParser()
        )
    }

    /**
     * Companion object for feature installation
     */
    companion object Feature : HttpClientFeature<Config, JsoupFeature> {
        override val key: AttributeKey<JsoupFeature> = AttributeKey("Jsoup")

        override fun prepare(block: Config.() -> Unit): JsoupFeature =
            JsoupFeature(Config().apply(block).parsers)

        override fun install(feature: JsoupFeature, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel)
                    return@intercept

                if (!info.type.java.isAssignableFrom(Document::class.java))
                    return@intercept

                val responseContentType = context.response.contentType() ?: return@intercept

                val parser = feature.parsers.firstNotNullOfOrNull { (type, parser) ->
                    parser.takeIf { responseContentType.match(type) }
                } ?: return@intercept

                val bodyContent = body.readRemaining().readText()
                val baseUri = context.request.url.toString()

                /* Jsoup Parsers internally contain a stateful TreeBuilder,
                   We need to create a deep copy to avoid issues with
                   concurrency */
                val document = Jsoup.parse(bodyContent, baseUri, parser.newInstance())
                proceedWith(HttpResponseContainer(info, document))
            }
        }
    }
}

/**
 * Install [JsoupFeature]
 */
@Suppress("FunctionName")
fun HttpClientConfig<*>.Jsoup(block: JsoupFeature.Config.() -> Unit = {}) {
    install(JsoupFeature, block)
}
