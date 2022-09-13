package org.climatechangemakers.hoa.webhook

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface LumaService {

  suspend fun getEvents(): List<LumaEvent>

  suspend fun getGuestsForEvent(eventSecret: String): List<LumaEventGuest>
}

class KtorLumaService(
  private val client: HttpClient,
  private val json: Json,
  private val lumaApiKey: String,
) : LumaService {

  override suspend fun getEvents(): List<LumaEvent> {
    val response = client.get(urlString = "https://api.lu.ma/user/events") {
      timeout { requestTimeoutMillis = 60_000}
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

  override suspend fun getGuestsForEvent(eventSecret: String): List<LumaEventGuest> = buildList {
    var nextCursor: String? = null
    do {
      val response = client.get(urlString = "https://api.lu.ma/event/guests") {
        timeout { requestTimeoutMillis = 60_000}
        parameter("secret", eventSecret)
        nextCursor?.let { parameter("pagination_cursor", it) }
      }

      val guests = json.decodeFromString(LumaEventGuestsWrapper.serializer(), response.bodyAsText())
      guests.entries.let(this::addAll)
      nextCursor = guests.nextCursor
    } while (nextCursor != null)
  }
}

@Serializable private class LumaEventWrapper(
  val events: List<LumaEvent>,
)

@Serializable private class LumaEventGuestsWrapper(
  val entries: List<LumaEventGuest>,
  @SerialName("next_cursor") val nextCursor: String?
)

