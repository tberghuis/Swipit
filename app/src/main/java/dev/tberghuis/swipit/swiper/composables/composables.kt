package dev.tberghuis.swipit.swiper.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun rememberIsForeground(): State<Boolean> {
  val isForeground = remember { mutableStateOf(true) }
  val lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current

  DisposableEffect(lifeCycleOwner) {
    val observer = LifecycleEventObserver { source, event ->
      when (event) {
        Lifecycle.Event.ON_PAUSE -> isForeground.value = false
        Lifecycle.Event.ON_RESUME -> isForeground.value = true
        else -> Unit
      }
    }
    lifeCycleOwner.lifecycle.addObserver(observer)

    onDispose {
      lifeCycleOwner.lifecycle.removeObserver(observer)
    }
  }
  return isForeground
}