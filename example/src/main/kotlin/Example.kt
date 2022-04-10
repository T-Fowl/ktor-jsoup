import com.tfowl.ktor.client.features.JsoupPlugin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

suspend fun main() {
    HttpClient() {
        install(JsoupPlugin) {
            parsers[ContentType.Application.Rss] = Parser.xmlParser()
        }
    }.use { client ->
        val feed: Document = client.get("https://xkcd.com/rss.xml").body()

        feed.select("rss>channel>item").forEach { item ->
            val title = item.selectFirst("title")?.text()
            val link = item.selectFirst("link")?.text()

            println("$title ($link)")
        }
    }
}