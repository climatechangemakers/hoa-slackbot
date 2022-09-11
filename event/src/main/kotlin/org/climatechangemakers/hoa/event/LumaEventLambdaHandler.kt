package org.climatechangemakers.hoa.event

import kotlinx.datetime.toJavaInstant
import org.climatechangemakers.hoa.event.database.Database
import org.climatechangemakers.hoa.event.database.HourOfActionEventQueries
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runtime.RawLambdaHandler
import java.time.ZoneOffset

class LumaEventLambdaHandler(
  database: Database,
  private val lumaService: LumaService,
) : RawLambdaHandler {

  private val hourOfActionEventQueries = database.hourOfActionEventQueries

  override suspend fun invoke(request: RawRequest): RawResponse {
    val events = lumaService.getEvents().filter { it.secret != null }
    hourOfActionEventQueries.insertEvents(events)
    println("Inserted ${events.size} events.")
    return RawResponse("")
  }
}

private fun HourOfActionEventQueries.insertEvents(events: List<LumaEvent>) = transaction {
  events.forEach { insertLumaEvent(it) }
}

private fun HourOfActionEventQueries.insertLumaEvent(event: LumaEvent) {
  insertEvent(
    id = event.id,
    eventName = event.name,
    eventStart = event.startTime.toJavaInstant().atOffset(ZoneOffset.UTC),
    secret = event.secret!!,
  )
}
