package org.climatechangemakers.lambda.runtime

internal enum class LambdaEnvironmentVariable(val key: String) {

  /**
   * The host and port of the runtime API.
   */
  LambdaRuntimeAPI("AWS_LAMBDA_RUNTIME_API"),
}

internal fun getEnvironmentVariable(variable: LambdaEnvironmentVariable): String {
  return checkNotNull(getEnvironmentVariable(variable.key)) {
    "No environment variable defined for ${variable.key}."
  }
}

internal expect fun getEnvironmentVariable(key: String): String?