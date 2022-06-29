package org.climatechangemakers.hoa.webhook

import kotlinx.coroutines.runBlocking
import org.climatechangemakers.lambda.model.InvocationRequest
import org.climatechangemakers.lambda.model.InvocationResponse
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  runLambda(::handleRequest)
}

private suspend fun handleRequest(request: InvocationRequest): InvocationResponse {
  println(request.payload)
  return InvocationResponse(request.payload)
}
