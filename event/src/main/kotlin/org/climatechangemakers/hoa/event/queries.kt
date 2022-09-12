package org.climatechangemakers.hoa.event

import app.cash.sqldelight.Query
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.climatechangemakers.hoa.event.database.HourOfActionEventGuestQueries
import org.climatechangemakers.hoa.event.database.HourOfActionEventQueries
import org.climatechangemakers.hoa.event.database.SelectUnsynced
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.minutes

fun HourOfActionEventQueries.insertEvents(events: List<LumaEvent>) = transaction {
  events.forEach { insertLumaEvent(it) }
}

fun HourOfActionEventQueries.insertLumaEvent(event: LumaEvent) {
  insertEvent(
    id = event.id,
    eventName = event.name,
    eventStart = event.startTime.toJavaInstant().atOffset(ZoneOffset.UTC),
    eventEnd = (event.startTime + event.durationMinutes!!.minutes).toJavaInstant().atOffset(ZoneOffset.UTC),
    secret = requireNotNull(event.secret) { "Event id:${event.id} secret is null." },
  )
}

fun HourOfActionEventQueries.selectedUnsynced(now: Instant): Query<SelectUnsynced> {
  return selectUnsynced(now.toJavaInstant().atOffset(ZoneOffset.UTC))
}

fun HourOfActionEventGuestQueries.insertGuest(guest: LumaEventGuest) {
  insert(
    eventId = guest.eventId,
    fullName = guest.fullName,
    email = guest.email,
    status = guest.approvalStatus,
    hasJoinedEvent = guest.hasJoinedEvent,
  )
}
