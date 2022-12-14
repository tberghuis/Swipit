package dev.tberghuis.swipit.tmp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tberghuis.swipit.data.RedgifsService
import dev.tberghuis.swipit.util.logd
import io.ktor.client.HttpClient
import javax.inject.Inject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

@HiltViewModel
class RedgifsViewModel @Inject constructor(
  app: Application,
  httpClient: HttpClient
) : ViewModel() {

  private val redgifsService: RedgifsService

  // manual DI forthewin
  val playerVMC: TmpPlayerVMC
  // if PlayerVMC needed this
//  val playerVMC by lazy { PlayerVMC(this) }

  init {
//    val scope = viewModelScope
    logd("viewModelScope $viewModelScope")
    logd("RedgifsViewModel $this")
    playerVMC = TmpPlayerVMC(app, viewModelScope)
    redgifsService = RedgifsService(httpClient)
  }

  fun getGifUrl() {
    viewModelScope.launch {
      redgifsService.getHdUrl("https://redgifs.com/watch/superiorblanchedalmondbrocketdeer")
    }
  }
}

// VMC ViewModelComponent
class TmpPlayerVMC(
  app: Application, scope: CoroutineScope
) {
  val player = ExoPlayer.Builder(app).build()

  // stateflow playing boolean
  val playingStateFlow = MutableStateFlow(false)

  val asyncRedgifUrl: Deferred<String>

  init {
    asyncRedgifUrl = scope.async {
      delay(2000)
      "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
  }
}