package org.climatechangemakers.hoa.attendance

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

  private val json = Json.Default

  @Test fun `deserializes correctly`() {
    val jsonString = """
      |{
      |  "full_name": "Kevin Cianfarini",
      |  "email": "g@gmail.com",
      |  "status": "approved",
      |  "event_start_time": "2022-07-09T12:00:00.000Z",
      |  "has_joined_event": true
      |}
    """.trimMargin()

    assertEquals(
      expected = LumaEventAttendanceRequest(
        fullName = "Kevin Cianfarini",
        email = "g@gmail.com",
        status = "approved",
        eventStartTime = Instant.parse("2022-07-09T12:00:00.000Z"),
        hasJoined = true,
      ),
      actual = json.decodeFromString(
        deserializer = LumaEventAttendanceRequest.serializer(),
        string = jsonString,
      )
    )
  }
}