package org.climatechangemakers.hoa.slackbot

import kotlinx.coroutines.runBlocking
import org.climatechangemakers.lambda.model.InvocationRequest
import org.climatechangemakers.lambda.model.InvocationResponse
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  runLambda(::handleRequest)
}

private suspend fun handleRequest(request: InvocationRequest): InvocationResponse {
  return InvocationResponse(request.payload)
}
