package org.climatechangemakers.lambda.runtime

import io.ktor.utils.io.core.*
import org.climatechangemakers.lambda.model.InvocationRequest
import org.climatechangemakers.lambda.model.InvocationResponse
import org.climatechangemakers.lambda.service.AwsService
import org.climatechangemakers.lambda.service.KtorAwsService

/**
 * Run [handler] for each invocation the AWS Lambda runtime receives. This function
 * sets up an internal event loop in which [handler] can be invoked multiple times.
 *
 * Any static setup that should not be recomputed on each invocation to [handler],
 * such as database connection setup, should be configured before the call to this
 * function.
 *
 * Implementations of [handler] should throw [LambdaHandlerException] in the event of an error.
 * These exceptions will be caught by the runtime and reported to the appropriate AWS Lambda
 * error API. Uncaught exceptions from [handler] will be caught and only their stack trace will
 * be reported.
 */
public suspend fun runLambda(
  handler: suspend (InvocationRequest) -> InvocationResponse,
): Unit = runLambdaInternal(
  service = KtorAwsService(),
  handler = handler,
)

internal suspend fun runLambdaInternal(
  service: AwsService,
  handler: suspend (InvocationRequest) -> InvocationResponse,
): Unit = service.use { aws ->
  while (true) {
    runOnce(service = aws, handler = handler)
  }
}

internal suspend fun runOnce(
  service: AwsService,
  handler: suspend (InvocationRequest) -> InvocationResponse,
) {
  try {
    val event = service.next()
    handleEvent(event, service, handler)?.let { successfulResponse ->
      service.respond(requestId = event.requestId, response = successfulResponse)
    }
  } catch (e: Throwable) {
    service.reportInitializationError(e)
  }
}

private suspend fun handleEvent(
  event: InvocationRequest,
  service: AwsService,
  handler: suspend (InvocationRequest) -> InvocationResponse,
): InvocationResponse? = try {
  handler(event)
} catch (e: LambdaHandlerException) {
  service.reportInvocationError(requestId = event.requestId, error = e)
  null
} catch (e: Throwable) {
  service.reportInvocationError(requestId = event.requestId, error = LambdaHandlerException(cause = e))
  null
}