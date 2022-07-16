package org.climatechangemakers.lambda.fake

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.LambdaHandlerException
import org.climatechangemakers.lambda.service.AwsService

class FakeAwsService : AwsService {

  override fun close() = Unit

  private val nextChannel = Channel<() -> RawRequest>(Channel.BUFFERED)
  private val responseErrors = Channel<Throwable>(Channel.BUFFERED)

  val initializationErrors = MutableSharedFlow<Throwable>(replay = 0, extraBufferCapacity = 10)
  val invocationExceptions = MutableSharedFlow<Pair<String, LambdaHandlerException>>(replay = 0, extraBufferCapacity = 10)
  val rawResponses = MutableSharedFlow<Pair<String, RawResponse>>(replay = 0, extraBufferCapacity = 10)

  fun queueNext(next: () -> RawRequest) {
    nextChannel.trySend(next)
  }

  fun queueResponseError(e: Throwable) = responseErrors.trySend(e)

  override suspend fun next(): RawRequest {
    return nextChannel.receive().invoke()
  }

  override suspend fun respond(requestId: String, response: RawResponse) {
    responseErrors.tryReceive().getOrNull()?.let { throw it }
    rawResponses.emit(requestId to response)
  }

  override suspend fun reportInvocationError(requestId: String, error: LambdaHandlerException) {
    invocationExceptions.emit(requestId to error)
  }

  override suspend fun reportInitializationError(error: Throwable) {
    initializationErrors.emit(error)
  }
}