package org.climatechangemakers.hoa.attendance

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
      |  "event_id": "some_id",
      |  "has_joined_event": true
      |}
    """.trimMargin()

    assertEquals(
      expected = LumaEventAttendanceRequest(
        fullName = "Kevin Cianfarini",
        email = "g@gmail.com",
        status = "approved",
        eventId = "some_id",
        hasJoined = true,
      ),
      actual = json.decodeFromString(
        deserializer = LumaEventAttendanceRequest.serializer(),
        string = jsonString,
      )
    )
  }
}