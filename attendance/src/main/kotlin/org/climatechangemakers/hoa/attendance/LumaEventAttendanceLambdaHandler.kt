package org.climatechangemakers.hoa.attendance

import kotlinx.serialization.json.Json
import org.climatechangemakers.hoa.attendance.database.Database
import org.climatechangemakers.hoa.attendance.database.HourOfActionEventAttendanceQueries
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.runtime.ApiGatewayV2LambdaHandler

class LumaEventAttendanceLambdaHandler(
  database: Database,
) : ApiGatewayV2LambdaHandler {

  private val json = Json { explicitNulls = false }
  private val attendanceQueries = database.hourOfActionEventAttendanceQueries

  override suspend fun invoke(request: ApiGatewayRequestV2): ApiGatewayResponseV2 {
    val record = request.body?.let { body ->
      json.decodeFromString(LumaEventAttendanceRequest.serializer(), body)
    }

    return when (record) {
      null -> ApiGatewayResponseV2(statusCode = 400)
      else -> {
        attendanceQueries.insertRecord(record)
        ApiGatewayResponseV2(statusCode = 202)
      }
    }
  }
}

private fun HourOfActionEventAttendanceQueries.insertRecord(
  record: LumaEventAttendanceRequest,
) = insert(
  eventId = record.eventId,
  fullName = record.fullName,
  email = record.email,
  status = record.status,
  hasJoinedEvent = record.hasJoined,
)
