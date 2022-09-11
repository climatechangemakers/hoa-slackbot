package org.climatechangemakers.hoa.event

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LumaEvent(
  @SerialName("api_id") val id: String,
  val name: String,
  @SerialName("secret_key") val secret: String?,
  @SerialName("start_at") val startTime: Instant,
)