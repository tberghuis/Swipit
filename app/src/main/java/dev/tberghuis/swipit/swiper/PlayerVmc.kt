package dev.tberghuis.swipit.swiper

import android.app.Application
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dev.tberghuis.swipit.data.SwiperUrl
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.flow.StateFlow

class PlayerVmc(
  private val swiperUrlListStateFlow: StateFlow<List<SwiperUrl>?>, app: Application,
) {
  private val swiperUrlList: List<SwiperUrl>
    get() = swiperUrlListStateFlow.value!!

  private val players: List<ExoPlayer>

  init {
    fun buildPlayer(): ExoPlayer {
      val httpDataSourceFactory = DefaultHttpDataSource.Factory()
      val dataSourceFactory: DataSource.Factory = DataSource.Factory {
        val dataSource: HttpDataSource = httpDataSourceFactory.createDataSource()
        dataSource.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36"
        )
        dataSource
      }

      return ExoPlayer.Builder(app).setMediaSourceFactory(
        DefaultMediaSourceFactory(app).setDataSourceFactory(
          dataSourceFactory
        )
      ).build().apply {
        playWhenReady = false
        repeatMode = Player.REPEAT_MODE_ONE
      }
    }
    players = listOf(buildPlayer(), buildPlayer(), buildPlayer())
  }

  fun getPlayer(page: Int): ExoPlayer {
    val playerIndex = page % 3
    return players[playerIndex]
  }

  fun setPlayerMediaItem(page: Int) {
    val playerIndex = page % 3
    val url = swiperUrlList[page].mediaUrl
    logd("player url $url")
    players[playerIndex].apply {
      val mediaItem =
        MediaItem.Builder().setUri(swiperUrlList[page].mediaUrl).setMimeType(MimeTypes.VIDEO_MP4)
          .build()
      setMediaItem(mediaItem)
      prepare()
    }
  }

  fun playerClick(page: Int) {
//    players[page%3].play()
    if (players[page % 3].isPlaying) {
      players[page % 3].pause()
    } else {
      players[page % 3].play()
    }
  }

  fun onCleared() {
    players.forEach {
      it.release()
    }
  }
}