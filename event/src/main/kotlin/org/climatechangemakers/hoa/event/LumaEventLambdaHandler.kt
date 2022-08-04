package org.climatechangemakers.hoa.event

import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.json.Json
import org.climatechangemakers.hoa.event.database.Database
import org.climatechangemakers.hoa.event.database.HourOfActionEventQueries
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.runtime.ApiGatewayV2LambdaHandler
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class LumaEventLambdaHandler(
  database: Database,
) : ApiGatewayV2LambdaHandler {

  private val json = Json.Default
  private val hourOfActionEventQueries = database.hourOfActionEventQueries

  override suspend fun invoke(request: ApiGatewayRequestV2): ApiGatewayResponseV2 {
    val lumaEvent = request.body?.let { body ->
      json.decodeFromString(LumaEvent.serializer(), body)
    }

    return when (lumaEvent) {
      null -> ApiGatewayResponseV2(statusCode = 400)
      else -> {
        hourOfActionEventQueries.insertLumaEvent(lumaEvent)
        ApiGatewayResponseV2(statusCode = 202)
      }
    }
  }
}

private fun HourOfActionEventQueries.insertLumaEvent(event: LumaEvent) {
  insertEvent(
    id = event.id,
    eventName = event.name,
    eventStart = event.startTime
      .toJavaInstant()
      .atOffset(ZoneOffset.UTC)
      .truncatedTo(ChronoUnit.MINUTES), // Do this in Kotlin because SQLDelight doesn't currently support pgsql DATE_TRUNC.
  )
}
