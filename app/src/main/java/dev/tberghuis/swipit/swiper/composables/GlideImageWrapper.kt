package dev.tberghuis.swipit.swiper.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageWrapper(url: String) {
  GlideImage(
    model = url,
    contentDescription = "image",
    modifier = Modifier.fillMaxSize(),
  )
}