package dev.tberghuis.swipit.swiper

import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import dev.tberghuis.swipit.MainActivity
import dev.tberghuis.swipit.data.SuGif
import dev.tberghuis.swipit.data.SuImage
import dev.tberghuis.swipit.data.SuVideo
import dev.tberghuis.swipit.swiper.composables.GlideGifView
import dev.tberghuis.swipit.swiper.composables.GlideImageWrapper
import dev.tberghuis.swipit.swiper.composables.PlayerViewWrapper
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Swiper(
) {
  val vm = hiltViewModel<SwiperViewModel>()
  val urlList by vm.swiperUrlListStateFlow.collectAsState(initial = null)
  val pagerState = rememberPagerState()

  if (urlList == null) {
    return
  }

  if (pagerState.currentPage >= urlList!!.size - 2) {
    LaunchedEffect(Unit) {
      vm.swiperUrlListVmc.fetchMore()
    }
  }

  val scope = rememberCoroutineScope()
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  VerticalPager(
    count = urlList!!.size,
    modifier = Modifier
      .onPreviewKeyEvent { keyEvent ->
        // this is bad side effect
        focusRequester.requestFocus()

        if (keyEvent.nativeKeyEvent.action != ACTION_UP) {
          return@onPreviewKeyEvent false
        }

        logd("keyEvent $keyEvent")
        when (keyEvent.nativeKeyEvent.keyCode) {
          KEYCODE_DPAD_UP, KEYCODE_DPAD_LEFT -> {
            logd("KEYCODE_DPAD_UP, KEYCODE_DPAD_LEFT")
            if (pagerState.currentPage > 0) {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
              }
            }
            true
          }
          KEYCODE_DPAD_DOWN, KEYCODE_DPAD_RIGHT -> {
            logd("KEYCODE_DPAD_DOWN, KEYCODE_DPAD_RIGHT")
            if (pagerState.currentPage < pagerState.pageCount - 1) {
              scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
              }
            }
            true
          }
          KEYCODE_DPAD_CENTER -> {
            vm.playerVmc.playerClick(pagerState.currentPage)
            true
          }
          else -> false
        }

      }
      .focusRequester(focusRequester)
      .focusable(),
    state = pagerState,
//    key = { page ->
//      urlList!![page].mediaUrl
//    }
  ) { page ->
    val swiperUrl = urlList!![page]

    when (swiperUrl) {
      is SuImage -> {
        GlideImageWrapper(swiperUrl.mediaUrl)
      }
      is SuVideo -> {
        // this is because PlayerVmc has only 3 ExoPlayer
        // getPlayer -> players[page % 3]
        if (kotlin.math.abs(page - currentPage) > 1) {
          return@VerticalPager
        }
        PlayerViewWrapper(page)
      }
      is SuGif -> GlideGifView(page, swiperUrl.mediaUrl)
    }
  }
}