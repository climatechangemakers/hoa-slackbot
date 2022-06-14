package org.climatechangemakers.lambda.runtime

internal actual fun getEnvironmentVariable(key: String): String? {
  return System.getenv(key)
}