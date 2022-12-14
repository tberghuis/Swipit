package dev.tberghuis.swipit.swiper

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tberghuis.swipit.data.RedditService
import dev.tberghuis.swipit.data.RedgifsService
import dev.tberghuis.swipit.data.SwiperUrl
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SwiperViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  redditService: RedditService,
  redgifsService: RedgifsService,
  app: Application
) : ViewModel() {
  // how to best deal with async initialization
  // doitwrong with lateinit and manual DI

  val swiperUrlListStateFlow = MutableStateFlow<List<SwiperUrl>?>(null)

  lateinit var playerVmc: PlayerVmc
  lateinit var swiperUrlListVmc: SwiperUrlListVmc

  init {
    val subreddit = savedStateHandle.get<String>("subreddit")!!
    viewModelScope.launch {
      playerVmc = PlayerVmc(swiperUrlListStateFlow, app)
      swiperUrlListVmc = SwiperUrlListVmc(
        subreddit,
        swiperUrlListStateFlow,
        redditService,
        redgifsService,
        viewModelScope
      )
    }
  }

  override fun onCleared() {
    super.onCleared()
    playerVmc.onCleared()
  }
}