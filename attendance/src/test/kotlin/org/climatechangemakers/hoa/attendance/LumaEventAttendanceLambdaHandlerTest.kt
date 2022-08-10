package org.climatechangemakers.hoa.attendance

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.junit.Test
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class LumaEventAttendanceLambdaHandlerTest : TestContainerProvider() {

  private val handler = LumaEventAttendanceLambdaHandler(database)
  private val eventQueries = database.hourOfActionEventQueries

  @Test fun `records event`() = runBlocking {
    val request = request("""
      |{
      |  "full_name": "Kevin",
      |  "email": "kevin@kevin.com",
      |  "status": "approved",
      |  "event_id": "some_id",
      |  "has_joined_event": true
      |}
    """.trimMargin())
    eventQueries.insertEvent(
      id = "some_id",
      eventStart = OffsetDateTime.now(),
      eventName = "some name"
    )

    handler.invoke(request)
    val (eventId: String, hasJoined: Boolean) = queryByEmail("kevin@kevin.com")
    assertTrue(hasJoined)
    assertEquals(expected = "some_id", actual = eventId)
  }

  @Test fun `unique constraint conflict overwrites data`() = runBlocking {
    val request = request("""
      |{
      |  "full_name": "Kevin",
      |  "email": "kevin@kevin.com",
      |  "status": "approved",
      |  "event_id": "some_id",
      |  "has_joined_event": true
      |}
    """.trimMargin())

    val request2 = request("""
      |{
      |  "full_name": "New Name",
      |  "email": "kevin@kevin.com",
      |  "status": "approved",
      |  "event_id": "some_id",
      |  "has_joined_event": true
      |}
    """.trimMargin())
    eventQueries.insertEvent(
      id = "some_id",
      eventStart = OffsetDateTime.now(),
      eventName = "some name"
    )

    handler.invoke(request)
    handler.invoke(request2)

    val name = queryForNameByEmail("kevin@kevin.com")
    assertEquals(expected = "New Name", actual = name)
  }

  private fun queryByEmail(email: String): Pair<String, Boolean> = driver.executeQuery(
    identifier = 0,
    sql = "SELECT event_id, has_joined_event FROM hour_of_action_event_attendance WHERE email = ?;",
    mapper = { cursor ->
      cursor.next()
      Pair(cursor.getString(0)!!, cursor.getBoolean(1)!!)
    },
    parameters = 1,
  ) {
    bindString(0, email)
  }.value

  private fun queryForNameByEmail(email: String): String = driver.executeQuery(
    identifier = 0,
    sql = "SELECT full_name FROM hour_of_action_event_attendance WHERE email = ?;",
    mapper = { cursor ->
      cursor.also { it.next() }.getString(0)!!
    },
    parameters = 1,
  ) {
    bindString(0, email)
  }.value

  private fun request(body: String) = ApiGatewayRequestV2(
    version = "2.0",
    routeKey = "",
    rawPath = "",
    rawQueryString = "",
    headers = emptyMap(),
    requestContext = ApiGatewayRequestV2.RequestContext(
      accountId = "",
      apiId = "",
      domainPrefix = "",
      domainName = "",
      http = ApiGatewayRequestV2.RequestContext.HttpContext(
        method = "",
        path = "",
        protocol = "",
        sourceIp = "",
        userAgent = "",
      ),
      requestId = "",
      routeKey = "",
      time = "",
      timeEpoch = 0,
      stage = "",
    ),
    body = body,
    isBase64Encoded = false,
  )
}
