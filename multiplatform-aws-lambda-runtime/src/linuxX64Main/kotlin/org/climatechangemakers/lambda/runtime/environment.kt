package org.climatechangemakers.lambda.runtime

import kotlinx.cinterop.toKString
import platform.posix.getenv

internal actual fun getEnvironmentVariable(key: String): String? {
  return getenv(key)?.toKString()
}