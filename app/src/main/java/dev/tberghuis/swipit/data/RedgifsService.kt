package dev.tberghuis.swipit.data

import dev.tberghuis.swipit.util.logd
import dev.tberghuis.swipit.util.unwrapString
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import javax.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class RedgifsService @Inject constructor(
  private val httpClient: HttpClient,
) {
  private var asyncAuthToken: Deferred<String>? = null
  suspend fun getAuthToken(): String = coroutineScope {
    asyncAuthToken?.let {
      return@coroutineScope it.await()
    }
    val deferredToken = async {
      var authToken = "Bearer "
      flow {
        val url = "https://api.redgifs.com/v2/auth/temporary"
        val response: JsonObject = httpClient.get {
          url(url)
        }.body()
        val token = response["token"]!!.unwrapString()
        emit(token)
      }
        // todo onCompletion if error
        // reset asyncAuthToken = null
        // todo test error and .retry()
        .collect {
          authToken += it
        }
      logd("authToken $authToken")
      authToken
    }
    asyncAuthToken = deferredToken
    deferredToken.await()
  }

  suspend fun getHdUrl(pageUrl: String): String {
    val id = pageUrl.replace("https://redgifs.com/watch/", "")
    val url = "https://api.redgifs.com/v2/gifs/$id"
    val gif = authRequest(url)
    return gif["gif"]!!.jsonObject["urls"]!!.jsonObject["hd"]!!.unwrapString()
  }

  // do it wrong
  // in future how to reuse httpClient with diff headers
  private suspend fun authRequest(url: String): JsonObject {
    val token = getAuthToken()
    val ret: JsonObject = httpClient.get {
      headers {
        append(HttpHeaders.Authorization, token)
        append(
          HttpHeaders.UserAgent,
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        )
      }
      url(url)
    }.body()
    return ret
  }
}