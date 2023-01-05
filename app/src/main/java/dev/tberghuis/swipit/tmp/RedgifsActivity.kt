package dev.tberghuis.swipit.tmp

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RedgifsActivity : ComponentActivity() {

  var keyCodeFlow: MutableSharedFlow<Int>? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SwipitTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          KeyEventTest()
        }
      }
    }
  }


  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

    logd("keyCode: $keyCode")

    if (keyCodeFlow == null) {
      return super.onKeyUp(keyCode, event)
    }

    return when (keyCode) {
      // up,down,left,right,ok
      19, 20, 21, 22, 23 -> {
        lifecycleScope.launch {
          keyCodeFlow?.emit(keyCode)
        }
        true
      }
      else -> super.onKeyUp(keyCode, event)
    }
  }

}

class FakeVm: ViewModel() {
  val keyCodeFlow = MutableSharedFlow<Int>()

  init {
    viewModelScope.launch {
      keyCodeFlow.collect {
        logd("collect: $it")
      }
    }
  }

}

val fakeVm = FakeVm()

@Composable
fun KeyEventTest() {
  val context = LocalContext.current

  Text("key event test")

  DisposableEffect(context) {
    (context as RedgifsActivity).keyCodeFlow = fakeVm.keyCodeFlow
    onDispose {
      (context as RedgifsActivity).keyCodeFlow = null
    }
  }
}
