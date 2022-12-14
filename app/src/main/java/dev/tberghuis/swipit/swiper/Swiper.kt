package dev.tberghuis.swipit.swiper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import dev.tberghuis.swipit.data.SuGif
import dev.tberghuis.swipit.data.SuImage
import dev.tberghuis.swipit.data.SuVideo
import dev.tberghuis.swipit.swiper.composables.GlideGifView
import dev.tberghuis.swipit.swiper.composables.GlideImageWrapper
import dev.tberghuis.swipit.swiper.composables.PlayerViewWrapper

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

  VerticalPager(count = urlList!!.size, state = pagerState) { page ->
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

//@Composable
//fun KeyboardHandler() {
//  val context = LocalContext.current
//  val vm = hiltViewModel<SwiperViewModel>()
//
//  // this is poor mans way
//  // not reusable
//  // only temporary for dev fetch more f
//  DisposableEffect(Unit) {
//    val mainActivity = context as MainActivity
//    mainActivity.handleKeyUp = { keyCode, event ->
//      logd("keyCode $keyCode")
//      logd("event $event")
//
////      f=34
//      if (keyCode == 34) {
//        logd("34")
////        vm.fetchMoreSharedFlow.tryEmit(Unit)
//        vm.swiperUrlListVmc.fetchMore()
//        true
//      } else {
//        false
//      }
//    }
//    onDispose {
//      mainActivity.handleKeyUp = { _, _ -> false }
//    }
//  }
//}