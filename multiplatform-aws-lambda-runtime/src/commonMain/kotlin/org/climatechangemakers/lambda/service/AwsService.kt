package org.climatechangemakers.lambda.service

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.LambdaEnvironmentVariable
import org.climatechangemakers.lambda.runtime.LambdaHandlerException
import org.climatechangemakers.lambda.runtime.getEnvironmentVariable
import org.climatechangemakers.lambda.runtime.listStackTrace

internal interface AwsService : Closeable {

  /**
   * Get the next [RawRequest] that's queued up in AWS Lambda.
   */
  suspend fun next(): RawRequest

  /**
   * Respond to an invocation associated with [requestId] with [response].
   */
  suspend fun respond(
    requestId: String,
    response: RawResponse,
  )

  /**
   * Report that [error] occured for request with [requestId] to AWS Lambda.
   */
  suspend fun reportInvocationError(
    requestId: String,
    error: LambdaHandlerException,
  )

  suspend fun reportInitializationError(
    error: Throwable,
  )
}

internal class KtorAwsService : AwsService {

  private val host: String = getEnvironmentVariable(LambdaEnvironmentVariable.LambdaRuntimeAPI)
  private val json = Json { prettyPrint = true }
  private val client: HttpClient = HttpClient(CIO) {
    expectSuccess = true
    install(HttpTimeout)
  }

  override suspend fun next(): RawRequest {
    val response: HttpResponse = client.get(urlString = "http://$host/$API_VERSION/runtime/invocation/next") {
      // We never want to time out. This is "long polling" for the next incoming request. The AWS Lambda
      // runtime will decide when to end the process.
      timeout { requestTimeoutMillis = Long.MAX_VALUE }
    }
    return RawRequest(
      requestId = checkNotNull(response.headers[AwsLambdaHeader.RequestId.key]) {
        "AWS Lambda next invocation had no value for ${AwsLambdaHeader.RequestId.key}"
      },
      payload = response.bodyAsText(),
    )
  }

  override suspend fun respond(requestId: String, response: RawResponse) {
    client.post(urlString = "http://$host/$API_VERSION/runtime/invocation/$requestId/response") {
      setBody(response.payload)
    }
  }

  override suspend fun reportInvocationError(
    requestId: String,
    error: LambdaHandlerException,
  ) = reportError(
    url = "http://$host/$API_VERSION/runtime/invocation/$requestId/error",
    error = LambdaError(
      errorMessage = error.message,
      errorType = error.errorType,
      stackTrace = error.listStackTrace,
    )
  )

  override suspend fun reportInitializationError(error: Throwable) = reportError(
    url = "http://$host/$API_VERSION/runtime/init/error",
    error = LambdaError(
      errorMessage = error.message,
      errorType = error::class.qualifiedName,
      stackTrace = error.listStackTrace,
    )
  )

  private suspend fun reportError(url: String, error: LambdaError) {
    val errorString = json.encodeToString(serializer = LambdaError.serializer(), value = error)
    try {
      client.post(urlString = url) {
        contentType(ContentType.Application.Json)
        error.errorType?.also { header(AwsLambdaHeader.FunctionErrorType.key, it) }
        setBody(errorString)
      }
    } finally {
      println(errorString)
    }
  }

  override fun close() = client.close()

  private companion object {
    const val API_VERSION = "2018-06-01"
  }
}

@Serializable private class LambdaError(
  val errorMessage: String? = null,
  val errorType: String? = null,
  val stackTrace: List<String>,
)

private enum class AwsLambdaHeader(val key: String) {
  RequestId("Lambda-Runtime-Aws-Request-Id"),
  FunctionErrorType("Lambda-Runtime-Function-Error-Type"),
}