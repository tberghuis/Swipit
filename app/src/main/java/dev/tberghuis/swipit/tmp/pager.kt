package dev.tberghuis.swipit.tmp

import android.app.Application
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tberghuis.swipit.data.RedgifsService
import dev.tberghuis.swipit.util.logd
import javax.inject.Inject
import kotlin.math.absoluteValue
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class PagerViewModel @Inject constructor(
//  val redgifsService: RedgifsService,
  app: Application
) : ViewModel() {
  val urlList = listOf(
    "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
    "https://storage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
  )

  private val players: List<ExoPlayer>

  init {
    fun buildPlayer(): ExoPlayer {
      return ExoPlayer.Builder(app).build().apply {
        playWhenReady = false
        repeatMode = Player.REPEAT_MODE_ONE
      }
    }
    players = listOf(buildPlayer(), buildPlayer(), buildPlayer())
  }

  // todo make suspend, fetch url here
  // write as a test
  fun getPlayer(page: Int, url: String): ExoPlayer {
    val playerIndex = page % 3
    return players[playerIndex].apply {
      val mediaItem = MediaItem.Builder().setUri(url).setMimeType(MimeTypes.VIDEO_MP4).build()
      setMediaItem(mediaItem)
      prepare()
    }
  }

  fun pausePlayer(page: Int) {
    val playerIndex = page % 3
    players[playerIndex].pause()
  }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RedgifsPager() {
  val vm = hiltViewModel<PagerViewModel>()

  VerticalPager(count = vm.urlList.size) { page ->

    // todo when statement based on urlList[page]

    if ((page - currentPage).absoluteValue <= 1) {
      TmpPlayerViewWrapper(page, vm.urlList[page])
    }
  }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerScope.TmpPlayerViewWrapper(page: Int, url: String) {
  val vm = hiltViewModel<PagerViewModel>()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .border(BorderStroke(2.dp, Color.Red))
  ) {

    AndroidView(factory = {
      PlayerView(it).apply {
        useController = false
        player = vm.getPlayer(page, url)
      }
    }, update = {
      if (page == currentPage) {
        it.player?.play()
      } else {
        it.player?.pause()
      }
    })
  }


  DisposableEffect(Unit) {
    onDispose {
      vm.pausePlayer(page)
    }
  }

}

@Composable
fun <T> AsyncValue(suspendLambda: suspend () -> T, content: @Composable (T) -> Unit) {
  var t by remember { mutableStateOf<T?>(null) }
  val scope = rememberCoroutineScope()
  if (t != null) {
    content(t!!)
  }
  LaunchedEffect(Unit) {
    scope.launch {
      t = suspendLambda()
    }
  }
}