package dev.tberghuis.swipit.swiper

import dev.tberghuis.swipit.data.RedditService
import dev.tberghuis.swipit.data.RedgifsService
import dev.tberghuis.swipit.data.SwiperUrl
import dev.tberghuis.swipit.data.toSwiperUrl
import dev.tberghuis.swipit.util.flatMapConcatDropLatest
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// doitwrong
class SwiperUrlListVmc(
  subreddit: String,
  swiperUrlListStateFlow: MutableStateFlow<List<SwiperUrl>?>,
  redditService: RedditService,
  redgifsService: RedgifsService,
  private val viewModelScope: CoroutineScope
) {

  // represents event scrolling near the end
  private val fetchMoreSharedFlow = MutableSharedFlow<Unit>()

  private var after: String? = null

  init {
    val postDataListFlow = flow {
      // initial fetch
      val rfr = redditService.fetch(subreddit)
      after = rfr.after
      emit(rfr.postDataList)
      fetchMoreSharedFlow.flatMapConcatDropLatest {
        val rfr = redditService.fetch(subreddit, after)
        after = rfr.after
        flowOf(rfr.postDataList)
      }.collect() {
        emit(it)
      }
    }

    viewModelScope.launch {
      postDataListFlow.map { postDataList ->
        postDataList.map {
          async {
            it.toSwiperUrl(redgifsService)
          }
        }.map {
          it.await()
        }.filterNotNull()
      }
        .collect {
          logd("concatenate")
          // concatenate ....
          val swiperUrlList = (swiperUrlListStateFlow.value ?: emptyList()) + it
          swiperUrlListStateFlow.value = swiperUrlList
        }
    }
  }

  fun fetchMore() {
    viewModelScope.launch {
      fetchMoreSharedFlow.emit(Unit)
    }
  }
}