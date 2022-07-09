package org.climatechangemakers.hoa.webhook

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertFailsWith

class HandlerTest {

  private val json = Json.Default

  @Test fun `handler throws serialization error`() {
    runBlocking {
      assertFailsWith<SerializationException> {
        handleRequest(json, "invalid json")
      }
    }
  }
}