package org.climatechangemakers.hoa.event

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LumaEvent(
  val id: String,
  val name: String,
  @SerialName("start_time") val startTime: Instant,
)