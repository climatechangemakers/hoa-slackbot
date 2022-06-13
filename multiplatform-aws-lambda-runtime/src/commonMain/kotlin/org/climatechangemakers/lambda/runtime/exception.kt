package org.climatechangemakers.lambda.runtime

/**
 * An error which should only be thrown from an AWS Lambda registered handler. This exception is intended for error
 * reporting of a specific AWS Lambda invocation.
 */
public class LambdaHandlerException(
  public val errorType: String? = null,
  message: String? = null,
  cause: Throwable? = null,
) : Throwable(message, cause)

internal val Throwable.listStackTrace: List<String> get() = stackTraceToString().split("\n")


