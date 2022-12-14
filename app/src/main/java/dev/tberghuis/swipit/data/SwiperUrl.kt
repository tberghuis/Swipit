package dev.tberghuis.swipit.data

sealed class SwiperUrl {
  abstract val mediaUrl: String
}

data class SuVideo(override val mediaUrl: String) : SwiperUrl()
data class SuImage(override val mediaUrl: String) : SwiperUrl()
data class SuGif(override val mediaUrl: String) : SwiperUrl()

// TODO wrap SwiperUrl with Result, to deal with ioexceptions
suspend fun PostData.toSwiperUrl(redgifsService: RedgifsService): SwiperUrl? {
  return when (this) {
    is PdRedditGifMp4 ->{
       SuVideo(mp4Url)
    }
    is PdRedgifs -> {
      SuVideo(redgifsService.getHdUrl(pageUrl))
    }
    is PdRedditImg -> {
      SuImage(pageUrl)
    }
    is PdRedditGif -> {
      SuGif(pageUrl)
    }
    is PdGiphy -> {
      val l = this.pageUrl.replace("https://giphy.com/gifs/", "").split("-")
      val mediaUrl = "https://i.giphy.com/media/${l[l.lastIndex]}/giphy.mp4"
      SuVideo(mediaUrl)
    }

    is PdGfycat -> {
      // i should use regex as more readable
      val slug =
        (this as PdGfycat).thumbnailUrl.replace("https://thumbs.gfycat.com/", "").split("-")[0]
      SuVideo("https://giant.gfycat.com/$slug.mp4")
    }

    is PdRedditVideo -> {
      SuVideo(this.fallbackUrl)
    }

    is PdNone -> {
      null
    }
  }
}