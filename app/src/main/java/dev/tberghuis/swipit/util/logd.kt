package dev.tberghuis.swipit.util

import android.util.Log
import dev.tberghuis.swipit.BuildConfig

fun logd(s: String) {
  if (BuildConfig.DEBUG) {
    Log.d("xxx", s)
  }
}