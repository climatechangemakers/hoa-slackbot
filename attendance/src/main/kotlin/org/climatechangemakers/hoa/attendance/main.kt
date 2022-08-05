@file:JvmName("Main")

package org.climatechangemakers.hoa.attendance

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.runtime.ApiGatewayV2LambdaHandler
import org.climatechangemakers.lambda.runtime.runLambda

suspend fun main() {
  runLambda(LumaEventAttendanceLambdaHandler())
}

class LumaEventAttendanceLambdaHandler : ApiGatewayV2LambdaHandler {

  private val json = Json { explicitNulls = false }

  override suspend fun invoke(request: ApiGatewayRequestV2): ApiGatewayResponseV2 {
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
}
