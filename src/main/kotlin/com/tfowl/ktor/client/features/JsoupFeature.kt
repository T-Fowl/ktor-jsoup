package com.tfowl.ktor.client.features

import io.ktor.client.HttpClient
import io.ktor.client.features.HttpClientFeature
import io.ktor.client.response.HttpResponseContainer
import io.ktor.client.response.HttpResponsePipeline
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.AttributeKey
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.readRemaining
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import kotlin.reflect.full.isSuperclassOf

class JsoupFeature(val parsers: Map<ContentType, Parser>) {

    class Config {
        var parsers = mutableMapOf(
                ContentType.Text.Html to Parser.htmlParser(),
                ContentType.Text.Xml to Parser.xmlParser())
    }

    companion object Feature : HttpClientFeature<Config, JsoupFeature> {
        override val key: AttributeKey<JsoupFeature> = AttributeKey("Jsoup")

        override fun prepare(block: Config.() -> Unit): JsoupFeature =
                JsoupFeature(Config().apply(block).parsers)

        override fun install(feature: JsoupFeature, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel)
                    return@intercept
                if (!info.type.isSuperclassOf(Document::class))
                    return@intercept

                val matchingType = feature.parsers.keys.firstOrNull {
                    context.response.contentType()?.match(it) == true
                } ?: return@intercept

                val parser = feature.parsers.getValue(matchingType)

                proceedWith(HttpResponseContainer(info, parser.parseInput(body.readRemaining().readText(), "${context.request.url}")))
            }
        }
    }
}