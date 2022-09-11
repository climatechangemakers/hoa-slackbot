package org.climatechangemakers.hoa.event

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface LumaService {

  suspend fun getEvents(): List<LumaEvent>
}

class KtorLumaService(
  private val client: HttpClient,
  private val json: Json,
  private val lumaApiKey: String,
) : LumaService {

  override suspend fun getEvents(): List<LumaEvent> {
    val response = client.get(urlString = "https://api.lu.ma/user/events") {
      headers {
        append("x-luma-api-key", lumaApiKey)
        append(HttpHeaders.Accept, ContentType.Application.Json)
      }
    }

    return json.decodeFromString(
      deserializer = LumaEventWrapper.serializer(),
      string = response.bodyAsText(),
    ).events
  }
}

@Serializable private class LumaEventWrapper(
  val events: List<LumaEvent>,
)

