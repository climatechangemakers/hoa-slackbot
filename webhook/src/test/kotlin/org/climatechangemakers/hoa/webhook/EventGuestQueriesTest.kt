package org.climatechangemakers.hoa.webhook

import app.cash.sqldelight.db.SqlDriver
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals


class EventGuestQueriesTest : TestContainerProvider() {

  @Test fun `inserting duplicate for event overwrites`() {
    database.hourOfActionEventQueries.insertLumaEvent(
      LumaEvent("id", "name", "secret", Instant.fromEpochSeconds(0), 60)
    )
    database.hourOfActionEventGuestQueries.insertGuest(
      LumaEventGuest("id", "full name", "email", true, "approved")
    )
    database.hourOfActionEventGuestQueries.insertGuest(
      LumaEventGuest("id", "full name", "email", true, "approved")
    )

    assertEquals(expected = 1, actual = driver.count())
  }
}

private fun SqlDriver.count(): Int = executeQuery(
  identifier = 0,
  sql = "SELECT count(*) from hour_of_action_event_guest",
  mapper = { cursor -> cursor.also { it.next() }.getLong(0)!!.toInt() },
  parameters = 0
).value