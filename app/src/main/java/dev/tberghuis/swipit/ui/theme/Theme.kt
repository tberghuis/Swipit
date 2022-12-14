package dev.tberghuis.swipit.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
)

@SuppressLint("NewApi")
@Composable
fun SwipitTheme(
  isDarkTheme: Boolean = isSystemInDarkTheme(),
  isDynamicColor: Boolean = true,
  content: @Composable () -> Unit
) {
  val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
  val myColorScheme = when {
    dynamicColor && isDarkTheme -> {
      dynamicDarkColorScheme(LocalContext.current)
    }
    dynamicColor && !isDarkTheme -> {
      dynamicLightColorScheme(LocalContext.current)
    }
    isDarkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = myColorScheme,
    typography = Typography,
    content = content
  )
}
