package org.climatechangemakers.hoa.webhook

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.lang.IllegalArgumentException
import java.time.ZoneOffset
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class EventQueriesTest : TestContainerProvider() {

  @Test fun `inserts each event`() {
    val events = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
      LumaEvent(
        id = "bar",
        name = "bar",
        secret = "secret2",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
    )

    database.hourOfActionEventQueries.insertEvents(events)
    assertEquals(expected = 2, actual = driver.countEvents())
  }

  @Test fun `errors when event secret is null`() {
    val events = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
      LumaEvent(
        id = "bar",
        name = "bar",
        secret = null,
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
    )

    assertFailsWith<IllegalArgumentException>(message = "Event id:bar secret is null.") {
      database.hourOfActionEventQueries.insertEvents(events)
    }
  }

  @Test fun `truncates time to minute`() {
    val events = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.parse("2022-07-25T08:30:59Z"),
        durationMinutes = 60,
      ),
    )

    database.hourOfActionEventQueries.insertEvents(events)
    assertEquals(expected = "foo", actual = driver.queryByInstant(Instant.parse("2022-07-25T08:30:00Z")))
  }

  @Test fun `inserts end time`() {
    val events = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.parse("2022-07-25T08:30:59Z"),
        durationMinutes = 60,
      ),
    )

    database.hourOfActionEventQueries.insertEvents(events)
    assertEquals(expected = "foo", actual = driver.queryByEndTime(Instant.parse("2022-07-25T09:30:00Z")))
  }

  @Test fun `overrwrites row on conflict`() {
    val events = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret2",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
    )

    database.hourOfActionEventQueries.insertEvents(events)
    assertEquals(expected = 1, actual = driver.countEvents())
  }

  @Test fun `preserves true for synced`() {
    val initial = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      )
    )
    database.hourOfActionEventQueries.transaction {
      database.hourOfActionEventQueries.insertEvents(initial)
      database.hourOfActionEventQueries.markSynced("foo")
    }

    val subsequent = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret2",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      )
    )
    database.hourOfActionEventQueries.insertEvents(subsequent)
    assertTrue(driver.querySyncStatusById("foo") ?: false)
  }

  @Test fun `select unsynced omits synced events`() {
    val initial = listOf(
      LumaEvent(
        id = "foo",
        name = "foo",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
      LumaEvent(
        id = "bar",
        name = "bar",
        secret = "secret1",
        startTime = Instant.fromEpochSeconds(0),
        durationMinutes = 60,
      ),
    )

    database.hourOfActionEventQueries.transaction {
      database.hourOfActionEventQueries.insertEvents(initial)
      database.hourOfActionEventQueries.markSynced("foo")
    }

    assertEquals(
      expected = 1,
      actual = database
        .hourOfActionEventQueries
        .selectedUnsynced(Instant.fromEpochSeconds(4000))
        .executeAsList()
        .size
    )
  }

  @Test fun `select unsynced omits future events`() {

  }
}

private fun SqlDriver.countEvents(): Int = executeQuery(
  identifier = 0,
  sql = "SELECT COUNT(*) FROM hour_of_action_event",
  mapper = { cursor -> cursor.also { it.next() }.getLong(0)!!.toInt() },
  parameters = 0,
).value

private fun SqlDriver.queryByInstant(instant: Instant): String? = executeQuery(
  identifier = 0,
      sql = "SELECT id FROM hour_of_action_event WHERE start_time = ?",
  mapper = { cursor -> cursor.also { it.next() }.getString(0) },
  parameters = 1,
) {
  check(this is JdbcPreparedStatement)
  bindObject(0, instant.toJavaInstant().atOffset(ZoneOffset.UTC))
}.value

private fun SqlDriver.queryByEndTime(instant: Instant): String? = executeQuery(
  identifier = 0,
  sql = "SELECT id FROM hour_of_action_event WHERE end_time = ?",
  mapper = { cursor -> cursor.also { it.next() }.getString(0) },
  parameters = 1,
) {
  check(this is JdbcPreparedStatement)
  bindObject(0, instant.toJavaInstant().atOffset(ZoneOffset.UTC))
}.value

private fun SqlDriver.querySyncStatusById(id: String): Boolean? = executeQuery(
  identifier = 0,
  sql = "SELECT synced FROM hour_of_action_event WHERE id = ?",
  mapper = { cursor -> cursor.also { it.next() }.getBoolean(0) },
  parameters = 1,
) {
  bindString(0, id)
}.value