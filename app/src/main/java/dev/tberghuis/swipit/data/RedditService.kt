package dev.tberghuis.swipit.data

import dev.tberghuis.swipit.data.api.RedditResponse
import dev.tberghuis.swipit.util.logd
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

// i know bad naming
data class RedditFetchResponse(val postDataList: List<PostData>, val after: String?)

class RedditService @Inject constructor(
  private val httpClient: HttpClient
) {
  suspend fun fetch(subreddit: String, after: String? = null): RedditFetchResponse {
    logd("RedditpxService httpClient $httpClient")
    // should probably build url query with ktor???
    val url =
      if (after == null) "https://reddit.com/r/$subreddit.json" else "https://reddit.com/r/$subreddit.json?after=$after"
    val redditResponse: RedditResponse = httpClient.get { url(url) }.body()
    logd("body $redditResponse")
    val ret = redditResponse.data.children.map {
      it.toPostData()
    }
    return RedditFetchResponse(ret, redditResponse.data.after)
  }
}