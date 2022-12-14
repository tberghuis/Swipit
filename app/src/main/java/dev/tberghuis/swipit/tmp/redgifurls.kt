package dev.tberghuis.swipit.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dev.tberghuis.swipit.data.RedgifsService
import dev.tberghuis.swipit.util.logd
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun FetchRedgifUrls() {
  val scope = rememberCoroutineScope()
  Column {
    Button(onClick = {
      logd("fetchurls")
      fetchRedgifUrls(scope)
    }) {
      Text("fetchurls")
    }
  }
}

fun fetchRedgifUrls(scope: CoroutineScope) {
  val httpClient = HttpClient(Android) {
    install(ContentNegotiation) {
      json(Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
      })
    }
  }

  val url = "https://reddit.com/r/nsfw.json"

  scope.launch {
    val response: JsonObject = httpClient.get { url(url) }.body()
    logd("response $response")

    val children = response["data"]!!.jsonObject["children"]!!.jsonArray
    logd("children $children")

    val urls = children.map {
      it.jsonObject["data"]!!.jsonObject["url"]!!.jsonPrimitive.toString().replace("\"", "")
    }
    logd("urls $urls")

    val redgifIds = urls.filter {
      it.startsWith("https://redgifs.com/watch/")
    }.map {
      it.replace("https://redgifs.com/watch/", "")
    }

    logd("redgifIds $redgifIds")

//    val rs = RedgifsService(httpClient)
//    val hdurls = rs.getHdGifUrlList(redgifIds)
//    logd("hdurls $hdurls")
  }

}