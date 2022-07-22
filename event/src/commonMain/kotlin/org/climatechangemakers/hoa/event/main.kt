package org.climatechangemakers.hoa.event

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.runtime.ApiGatewayV2LambdaHandler
import org.climatechangemakers.lambda.runtime.runLambda

fun main() = runBlocking {
  runLambda(LumaEventLambdaHandler())
}

class LumaEventLambdaHandler : ApiGatewayV2LambdaHandler {

  private val json = Json.Default

  override suspend fun invoke(request: ApiGatewayRequestV2): ApiGatewayResponseV2 {
    val lumaEvent = request.body?.let { body ->
      json.decodeFromString(LumaEvent.serializer(), body)
    }
    println(lumaEvent)

    return ApiGatewayResponseV2(statusCode = 202)
  }
}