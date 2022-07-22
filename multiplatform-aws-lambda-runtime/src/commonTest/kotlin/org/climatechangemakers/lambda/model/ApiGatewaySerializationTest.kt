package org.climatechangemakers.lambda.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiGatewaySerializationTest {

  private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    encodeDefaults = true
  }

  @Test fun `V2 response serializes correctly`() = assertEquals(
    expected = """
      |{
      |  "statusCode": 200,
      |  "headers": {
      |    "header1": "value1"
      |  },
      |  "cookies": [
      |    "cookie1",
      |    "cookie2"
      |  ],
      |  "isBase64Encoded": false,
      |  "body": "Hello from Lambda!"
      |}
    """.trimMargin(),
    actual = json.encodeToString(
      serializer = ApiGatewayResponseV2.serializer(),
      value = ApiGatewayResponseV2(
        statusCode = 200,
        headers = mapOf("header1" to "value1"),
        cookies = listOf("cookie1", "cookie2"),
        isBase64Encoded = false,
        body = "Hello from Lambda!",
      )
    )
  )
}