package dev.tberghuis.swipit.data

import android.net.Uri
import android.os.Build
import android.text.Html
import dev.tberghuis.swipit.data.api.RedditResponsePost
import dev.tberghuis.swipit.util.logd
import java.net.URLDecoder

sealed class PostData {
  abstract val pageUrl: String
}

data class PdNone(override val pageUrl: String) : PostData()
data class PdRedgifs(override val pageUrl: String) : PostData()
data class PdRedditGifMp4(override val pageUrl: String, val mp4Url: String) : PostData()
data class PdRedditGif(override val pageUrl: String) : PostData()
data class PdRedditImg(override val pageUrl: String) : PostData()
data class PdRedditVideo(override val pageUrl: String, val fallbackUrl: String) : PostData()
data class PdGiphy(override val pageUrl: String) : PostData()
data class PdGfycat(override val pageUrl: String, val thumbnailUrl: String) : PostData()

fun RedditResponsePost.toPostData(): PostData {
  val url = data.url

  // todo refactor without when
  return when {
    url.startsWith("https://redgifs.com/watch/") -> PdRedgifs(url)

    isRedditGifMp4(this) -> {
//      val mp4Url = Uri.decode(this.data.preview?.images?.get(0)?.variants?.mp4?.source?.url!!)
      val mp4Url = this.data.preview?.images?.get(0)?.variants?.mp4?.source?.url!!.unescapeUrl()

      PdRedditGifMp4(
        url, mp4Url
      )
    }

    url.startsWith("https://i.redd.it/") && url.endsWith(".gif") -> PdRedditGif(url)

    isRedditImg(url) -> PdRedditImg(url)

    url.startsWith("https://giphy.com/gifs/") -> PdGiphy(url)

    url.startsWith("https://gfycat.com/") -> {
      logd("gfycat url $url")
      val tu = this.data.media?.oembed?.thumbnail_url
      if (tu != null) return PdGfycat(url, tu)
      val fu = this.data.preview?.reddit_video_preview?.fallback_url
      if (fu != null) return PdRedditVideo(url, fu)
      PdNone(url)
    }

    isRedditVideo(this) -> {
      val fu = this.data.media!!.reddit_video!!.fallback_url
      PdRedditVideo(url, fu!!)
    }

    else -> {
      logd("no postdata match")
      PdNone(url)
    }
  }
}

// is there a better pattern than this??? could i attach this to PdRedditImg ???
private fun isRedditImg(url: String): Boolean {
  if (!url.startsWith("https://i.redd.it/")) {
    return false
  }
  return url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")
}

private fun isRedditVideo(post: RedditResponsePost): Boolean {
  if (!post.data.url.startsWith("https://v.redd.it/")) {
    return false
  }
  return post.data.media?.reddit_video != null
}

// "url": "https://i.redd.it/g0nyfsnyy85a1.gif",
// todo refactor return PostData?
private fun isRedditGifMp4(post: RedditResponsePost): Boolean {
  if (!post.data.url.startsWith("https://i.redd.it/")) return false
  val mp4Url = post.data.preview?.images?.get(0)?.variants?.mp4?.source?.url
  return mp4Url != null
}

private fun String.unescapeUrl(): String {
  val r = if (Build.VERSION.SDK_INT >= 24) Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
  else Html.fromHtml(this)
  return r.toString()
}