package org.climatechangemakers.hoa.event

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {

  val json = Json.Default
  runLambda(json) { request ->
    val lumaEvent = request.body?.let { body ->
      json.decodeFromString(LumaEvent.serializer(), body)
    }
    println(lumaEvent)

    ApiGatewayResponseV2(
      statusCode = 202,
      headers = emptyMap(),
      cookies = emptyList(),
      isBase64Encoded = false,
      body = null,
    )
  }
}