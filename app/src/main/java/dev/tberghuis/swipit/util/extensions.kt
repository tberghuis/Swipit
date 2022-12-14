package dev.tberghuis.swipit.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

fun <T, R> Flow<T>.flatMapConcatDropLatest(transform: suspend (value: T) -> Flow<R>): Flow<R> {
  return flow {
    channelFlow {
      collect { upstreamValue ->
        trySend(upstreamValue)
      }
    }.buffer(0).collect { upstreamValue ->
      val downstreamFlow = transform(upstreamValue)
      downstreamFlow.collect() {
        emit(it)
      }
    }
  }
}

fun JsonElement.unwrapString(): String {
  val s = jsonPrimitive.toString()
  return s.substring(1, s.length - 1)
}