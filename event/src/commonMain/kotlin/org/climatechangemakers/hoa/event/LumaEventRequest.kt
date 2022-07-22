package org.climatechangemakers.hoa.event

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class LumaEvent(
  val id: String,
  val name: String,
  @SerialName("start_time") val startTime: Instant,
)