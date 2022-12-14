package dev.tberghuis.swipit.tmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.Deferred

@AndroidEntryPoint
class RedgifsActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SwipitTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
//          RedgifsScreen()
//          RedgifsPager()
//          FetchRedgifUrls()
//          DownloadScreen()
//          TmpGifsUrls()
          HardcodePlayer()
        }
      }
    }
  }
}

@Composable
fun RedgifsScreen() {
  val vm = hiltViewModel<RedgifsViewModel>()
  Column {
    Text("hello redgifs")
    Button(onClick = {
      vm.getGifUrl()
    }) {
      Text("getGifUrl")
    }
    AwaitRedgifUrl()
  }
}

@Composable
fun AwaitRedgifUrl() {
  val vm = hiltViewModel<RedgifsViewModel>()
  AwaitDeferred(vm.playerVMC.asyncRedgifUrl) { url ->
    RedgifsPlayer(url)
  }
}


@Composable
fun <T> AwaitDeferred(deferred: Deferred<T>, content: @Composable (T) -> Unit) {
  var t by remember { mutableStateOf<T?>(null) }
  if (t != null) {
    content(t!!)
  }
  LaunchedEffect(Unit) {
    // optional just start async here
    t = deferred.await()
  }
}

@Composable
fun RedgifsPlayer(redgifUrl: String) {
  val vm = hiltViewModel<RedgifsViewModel>()
  val playing by vm.playerVMC.playingStateFlow.collectAsState()

  AndroidView(factory = { context ->
    PlayerView(context).also {
      val player = vm.playerVMC.player
      it.player = player
      val mi = MediaItem.fromUri(redgifUrl)
      player.setMediaItem(mi)
      player.prepare()
    }
  }, update = {
    it.player?.let { player ->
      when (playing) {
        true -> {
          player.play()
        }
        false -> {
          player.pause()
        }
      }

    }
  }, modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(16 / 9f)
  )

  Button(onClick = {
    logd("play")
    vm.playerVMC.playingStateFlow.value = true
  }) {
    Text("play")
  }
  Button(onClick = {
    logd("pause")
    vm.playerVMC.playingStateFlow.value = false
  }) {
    Text("pause")
  }
}