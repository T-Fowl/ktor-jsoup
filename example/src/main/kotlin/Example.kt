import com.tfowl.ktor.client.plugins.JsoupPlugin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jsoup.nodes.Document

suspend fun main() {
    HttpClient() {
        install(JsoupPlugin) {
            parseAsXml(ContentType.Application.Rss)
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