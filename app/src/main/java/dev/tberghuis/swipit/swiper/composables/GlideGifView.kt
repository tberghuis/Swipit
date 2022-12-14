package dev.tberghuis.swipit.swiper.composables

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.ImageViewTarget
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import dev.tberghuis.swipit.util.logd

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerScope.GlideGifView(page: Int, url: String) {
  var gifDrawable: GifDrawable? by remember { mutableStateOf(null) }

  Box(contentAlignment = Alignment.Center) {
    AndroidView(factory = {
      val imageView = ImageView(it)
      Glide.with(it).asGif().load(url).into(object : ImageViewTarget<GifDrawable>(imageView) {
        override fun setResource(resource: GifDrawable?) {
          gifDrawable = resource
          imageView.setImageDrawable(resource)
          logd("ImageViewTarget setResource")
        }
      })
      imageView
    }, modifier = Modifier
      .fillMaxSize()
      .clickable {
        gifDrawable?.let {
          if (it.isRunning) {
            it.stop()
          } else {
            it.start()
          }
        }
      }, update = {
      gifDrawable?.let {
        if (page == currentPage) {
          logd("page start $page")
          it.start()
        } else {
          logd("page stop $page")
          it.stop()
        }
      }
    })
    gifDrawable ?: CircularProgressIndicator()
  }
}