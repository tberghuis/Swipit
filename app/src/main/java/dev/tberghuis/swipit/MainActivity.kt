package dev.tberghuis.swipit

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import dev.tberghuis.swipit.util.logd
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//  var keyCodeFlow = MutableSharedFlow<Int>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SwipitTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          SwipitNavHost()
        }
      }
    }
  }

//  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
//    logd("onKeyUp keyCode: $keyCode")
//    return when (keyCode) {
//      // up,down,left,right,ok
//      19, 20, 21, 22, 23 -> {
//        lifecycleScope.launch {
//          keyCodeFlow.emit(keyCode)
//        }
//        true
//      }
//      else -> super.onKeyUp(keyCode, event)
//    }
//  }


}