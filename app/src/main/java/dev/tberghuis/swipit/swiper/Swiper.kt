package dev.tberghuis.swipit.swiper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Swiper(
) {
  val vm = hiltViewModel<SwiperViewModel>()
  val urlList by vm.swiperUrlListStateFlow.collectAsState(initial = null)
  val pagerState = rememberPagerState()

//  val context = LocalContext.current
//
//  val keyCodeFlow = remember(context) {
//    MutableSharedFlow<Int>().apply {
//      (context as MainActivity).keyCodeFlow.collect {
//
//      }
//    }
//  }


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

  // tv remote control pager and player
  val context = LocalContext.current
//  DisposableEffect(context) {
//    (context as MainActivity).keyCodeFlow = keyCodeFlow
//    onDispose {
//      (context as MainActivity).keyCodeFlow = null
//    }
//  }

  LaunchedEffect(context) {
    (context as MainActivity).keyCodeFlow.collect { keyCode ->
      logd("collect: $keyCode")
      when (keyCode) {
        20, 22 -> { // down, right
          if (pagerState.currentPage < pagerState.pageCount - 1) {
            pagerState.scrollToPage(pagerState.currentPage + 1)
          }
        }
        else -> {

        }
      }
    }
  }


//  val context = LocalContext.current
//  val scope = rememberCoroutineScope()
//  DisposableEffect(context) {
//    (context as MainActivity).handleKeyUp = { keyCode ->
//      logd("handleKeyUp keyCode $keyCode")
//      when (keyCode) {
//        // up left
//        19, 21 -> {
//          // todo
//          true
//        }
//        // down, right
//        20, 22 -> {
//          // todo
//          if (pagerState.currentPage < pagerState.pageCount - 1) {
//            scope.launch {
//              pagerState.scrollToPage(pagerState.currentPage + 1)
//            }
//          }
//          true
//        }
//        // ok
//        23 -> {
//          // todo
//          true
//        }
//        else -> {
//          false
//        }
//      }
//    }
//    onDispose {
//      (context as MainActivity).handleKeyUp = { _ -> false }
//    }
//  }

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