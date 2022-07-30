package org.climatechangemakers.lambda.runtime

import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.service.AwsService
import org.climatechangemakers.lambda.service.KtorAwsService

internal val LambdaJson = Json {
  encodeDefaults = true
}

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
  handler: RawLambdaHandler,
): Unit = runLambdaInternal(
  service = KtorAwsService(LambdaJson),
  handler = handler,
)

public suspend fun runLambda(
  handler: ApiGatewayV2LambdaHandler,
): Unit = runLambdaInternal(KtorAwsService(LambdaJson), object : RawLambdaHandler {
  override suspend fun invoke(request: RawRequest): RawResponse {
    val gatewayRequest = LambdaJson.decodeFromString(ApiGatewayRequestV2.serializer(), request.payload)
    return RawResponse(
      LambdaJson.encodeToString(
        serializer = ApiGatewayResponseV2.serializer(),
        value = handler.invoke(gatewayRequest),
      )
    )
  }
})

internal suspend fun runLambdaInternal(
  service: AwsService,
  handler: RawLambdaHandler,
): Unit = service.use { aws ->
  while (true) {
    runOnce(service = aws, handler = handler)
  }
}

internal suspend fun runOnce(
  service: AwsService,
  handler: RawLambdaHandler,
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
  event: RawRequest,
  service: AwsService,
  handler: RawLambdaHandler,
): RawResponse? = try {
  handler.invoke(event)
} catch (e: LambdaHandlerException) {
  service.reportInvocationError(requestId = event.requestId, error = e)
  null
} catch (e: Throwable) {
  service.reportInvocationError(
    requestId = event.requestId,
    error = LambdaHandlerException(
      errorType = e::class.qualifiedName,
      cause = e,
      message = e.message
    ),
  )
  null
}