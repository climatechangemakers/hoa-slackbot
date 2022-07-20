package org.climatechangemakers.hoa.event

import kotlinx.coroutines.runBlocking
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  runLambda { request ->
    println(request.payload)
    RawResponse(request.payload)
  }
}