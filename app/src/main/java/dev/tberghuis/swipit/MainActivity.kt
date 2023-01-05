package dev.tberghuis.swipit

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  var handleKeyUp: (Int) -> Boolean = { _ -> false }

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

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return handleKeyUp(keyCode) || super.onKeyUp(keyCode, event)
  }
}