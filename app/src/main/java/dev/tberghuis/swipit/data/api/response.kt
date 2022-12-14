package dev.tberghuis.swipit.data.api

import kotlinx.serialization.Serializable

@Serializable
data class RedditResponse(
  val data: RedditResponseData,
)

// doitwrong
// must be a better way???? meh
@Serializable
data class RedditResponseData(
  // todo after
  val after: String?,
  val children: List<RedditResponsePost>,
)

@Serializable
data class RedditResponsePost(
  val data: RedditResponsePostData,
)

@Serializable
data class RedditResponsePostData(
  val media: RedditResponsePostDataMedia? = null, val url: String, val preview: RapiPreview? = null
)

@Serializable
data class RapiPreview(
  val reddit_video_preview: RapiRedditVideoPreview? = null, val images: List<RapiImage>? = null
)

@Serializable
data class RapiImage(
  val variants: RapiVariants? = null
)

@Serializable
data class RapiVariants(
  val mp4: RapiMp4? = null
)

@Serializable
data class RapiMp4(
  val source: RapiSource? = null
)

@Serializable
data class RapiSource(
  val url: String? = null
)


@Serializable
data class RapiRedditVideoPreview(
  val fallback_url: String?,
)


@Serializable
data class RedditResponsePostDataMedia(
  val oembed: RedditResponsePostDataMediaOembed? = null, val reddit_video: RapiRedditVideo? = null
)


@Serializable
data class RedditResponsePostDataMediaOembed(
  val thumbnail_url: String?,
)

@Serializable
data class RapiRedditVideo(
  val fallback_url: String?,
)