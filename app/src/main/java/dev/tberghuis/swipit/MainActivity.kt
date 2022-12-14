package dev.tberghuis.swipit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.tberghuis.swipit.ui.theme.SwipitTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  var handleKeyUp: (Int, android.view.KeyEvent?) -> Boolean = { _, _ -> false }

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

  override fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
    return handleKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)
  }
}