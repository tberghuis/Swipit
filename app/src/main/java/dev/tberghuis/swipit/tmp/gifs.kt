package dev.tberghuis.swipit.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import dev.tberghuis.swipit.data.RedditService
import dev.tberghuis.swipit.util.logd
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val httpClient = HttpClient(Android) {
  install(ContentNegotiation) {
    json(Json {
      prettyPrint = true
      isLenient = true
      ignoreUnknownKeys = true
    })
  }
}
private val redditService = RedditService(httpClient)

@Preview
@Composable
fun TmpGifsUrls() {
  val scope = rememberCoroutineScope()
  Column {
    Text("hello gifs urls")
    Button(onClick = {
      scope.launch {
        fetchrgifs()
      }
    }) {
      Text("fetchrgifs")
    }
  }
}


suspend fun fetchrgifs() {
  logd("fetchrgifs")
  val redditFetchResponse = redditService.fetch("memegifs")


  val urlList = redditFetchResponse.postDataList.map {
    it.pageUrl
  }


  logd("urlList $urlList")

}
