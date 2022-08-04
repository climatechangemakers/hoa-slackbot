package org.climatechangemakers.hoa.event

import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.junit.Test
import java.time.ZoneOffset
import kotlin.test.assertEquals

class LumaEventLambdaHandlerTest : TestContainerProvider() {

  private val handler = LumaEventLambdaHandler(database)

  @Test fun `event handler inserts luma event`() = runBlocking {
    val request = request("""
      |{
      |  "id": "some_id",
      |  "name": "This is an event name",
      |  "start_time": "2022-07-25T22:34:00Z"
      |}
    """.trimMargin())
    handler.invoke(request)

    assertEquals(
      expected = "some_id",
      actual = queryByInstant(Instant.parse("2022-07-25T22:34:00Z"))
    )
  }

  @Test fun `inserting ID conflict overwrites with new data`() = runBlocking {
    val request = request("""
      |{
      |  "id": "some_id",
      |  "name": "This is an event name",
      |  "start_time": "2022-07-25T22:35:00Z"
      |}
    """.trimMargin())
    val request2 = request("""
      |{
      |  "id": "some_id",
      |  "name": "This is an event name",
      |  "start_time": "2022-07-25T08:30:00Z"
      |}
    """.trimMargin())

    handler.invoke(request)
    handler.invoke(request2)

    assertEquals(
      expected = "some_id",
      actual = queryByInstant(Instant.parse("2022-07-25T08:30:00Z"))
    )
  }

  @Test fun `inserting start time with offset is valid`() = runBlocking {
    val request = request("""
      |{
      |  "id": "some_id",
      |  "name": "This is an event name",
      |  "start_time": "2022-07-25T20:30:00-04:00"
      |}
    """.trimMargin())

    handler.invoke(request)

    assertEquals(
      expected = "some_id",
      actual = queryByInstant(Instant.parse("2022-07-26T00:30:00Z"))
    )
  }

  @Test fun `inserting start time truncates to minute`() = runBlocking {
    val request = request("""
      |{
      |  "id": "some_id",
      |  "name": "This is an event name",
      |  "start_time": "2022-07-25T08:30:00Z"
      |}
    """.trimMargin())

    handler.invoke(request)

    assertEquals(
      expected = "some_id",
      actual = queryByInstant(Instant.parse("2022-07-25T08:30:00Z"))
    )
  }

  /**
   * Get the ID of a row when querying by [instant].
   */
  private fun queryByInstant(
    instant: Instant,
  ): String? = driver.executeQuery(
    identifier = 0,
    sql = "SELECT id FROM hour_of_action_event WHERE event_start = ?",
    mapper = { cursor ->
      cursor.also { it.next() }.getString(0)
    },
    parameters = 1,
  ) {
    check(this is JdbcPreparedStatement)
    bindObject(0, instant.toJavaInstant().atOffset(ZoneOffset.UTC))
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