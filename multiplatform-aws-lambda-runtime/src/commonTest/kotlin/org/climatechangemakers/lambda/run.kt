package org.climatechangemakers.lambda

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun runWithTimeout(
  timeout: Duration = 1.seconds,
  block: suspend () -> Unit,
): Unit = runBlocking {
  withTimeout(timeout) {
    block()
  }
}