package dev.tberghuis.swipit.tmp

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme
import dev.tberghuis.swipit.util.logd

@AndroidEntryPoint
class RedgifsActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SwipitTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          Text("hello world")
        }
      }
    }
  }


  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {

    logd("keyCode: $keyCode")

    return true

//    return when (keyCode) {
//      KeyEvent.KEYCODE_D -> {
//        true
//      }
//      KeyEvent.KEYCODE_F -> {
//        true
//      }
//      KeyEvent.KEYCODE_J -> {
//        true
//      }
//      KeyEvent.KEYCODE_K -> {
//        true
//      }
//      else -> super.onKeyUp(keyCode, event)
//    }
  }

}
