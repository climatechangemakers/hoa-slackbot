package org.climatechangemakers.hoa.attendance

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  val json = Json { this.explicitNulls = false }
  runLambda(json) { handleRequest(json, it) }
}

internal suspend fun handleRequest(
  json: Json,
  request: ApiGatewayRequestV2,
): ApiGatewayResponseV2 {
  return ApiGatewayResponseV2(
    statusCode = 202,
    headers = mapOf("Content-Type" to "application/json"),
    cookies = emptyList(),
    isBase64Encoded = false,
    body = request.body?.let { body ->
      json.decodeFromString(LumaEventAttendanceRequest.serializer(), body)
    }?.toString(),
  )
}
