package org.climatechangemakers.hoa.webhook

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.InvocationRequest
import org.climatechangemakers.lambda.model.InvocationResponse
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  val json = Json.Default

  runLambda { request ->
    handleRequest(json, request.payload)
  }
}

internal suspend fun handleRequest(json: Json, requestJson: String): InvocationResponse {
  val lumaRequest = json.decodeFromString(
    deserializer = LumaEventAttendanceRequest.serializer(),
    string = requestJson,
  )

  println(lumaRequest)

  return InvocationResponse(lumaRequest.toString())
}
