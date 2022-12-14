package dev.tberghuis.swipit.swiper.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import dev.tberghuis.swipit.swiper.SwiperViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerScope.PlayerViewWrapper(page: Int) {
  val vm = hiltViewModel<SwiperViewModel>()
  val isForeground = rememberIsForeground()
  val exoPlayer = remember(page) { vm.playerVmc.getPlayer(page) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .clickable {
        vm.playerVmc.playerClick(page)
      },
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AndroidView(factory = {
      vm.playerVmc.setPlayerMediaItem(page) // i could move to LaunchedEffect???
      PlayerView(it).apply {
        useController = false
        player = exoPlayer


      }
    }, update = {
      if (isForeground.value && page == currentPage) {
        it.player?.play()
      } else {
        it.player?.pause()
      }
    })

    TimeBar(page, exoPlayer)
  }
}

@Composable
fun TimeBar(page: Int, player: ExoPlayer) {
  // do i need page as key to remember??? i have no idea
  // todo run some tests to find out
  var currentPosition by remember(page) {
    mutableStateOf(0L)
  }
  var duration by remember(page) {
    mutableStateOf(0L)
  }
  var isPlaying by remember(page) { mutableStateOf(true) }

  val progress = if (duration <= 0) 0f else currentPosition.toFloat() / duration.toFloat()
  LinearProgressIndicator(
    progress = progress,
    modifier = Modifier.fillMaxWidth(),
  )

  // https://stackoverflow.com/questions/71868095/update-jetpack-compose-slider-progress-based-on-exoplayer-playing-audio-position
  // this is bloat
  DisposableEffect(page) {
    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(_isPlaying: Boolean) {
        isPlaying = _isPlaying
      }
    }
    player.addListener(listener)
    onDispose {
      player.removeListener(listener)
    }
  }

  if (isPlaying) {
    LaunchedEffect(page) {
      while (true) {
        currentPosition = player.currentPosition
        duration = player.duration
        delay(100)
      }
    }
  }
}