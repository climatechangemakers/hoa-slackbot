package org.climatechangemakers.hoa.webhook

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LumaEventAttendanceRequest(
  @SerialName("full_name") val fullName: String,
  val email: String,
  val status: String,
  @SerialName("event_start_time") val eventStartTime: Instant,
  @SerialName("has_joined_event") val hasJoined: Boolean,
)