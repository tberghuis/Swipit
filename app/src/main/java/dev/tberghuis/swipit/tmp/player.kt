package dev.tberghuis.swipit.tmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun HardcodePlayer() {
  HardcodePlayerContent("https://i.giphy.com/media/iBH30K5eDqHxS/giphy.mp4")
}

@Composable
fun HardcodePlayerContent(url: String) {
  val appContext = LocalContext.current.applicationContext
  val player = remember {
    ExoPlayer.Builder(appContext).build().apply {
      playWhenReady = true
      repeatMode = Player.REPEAT_MODE_ONE
    }
  }
  AndroidView(factory = { context ->
    PlayerView(context).also {
      it.player = player
      val mi = MediaItem.fromUri(url)
      player.setMediaItem(mi)
      player.prepare()
    }
  }, update = {}, modifier = Modifier.fillMaxSize()
  )
}