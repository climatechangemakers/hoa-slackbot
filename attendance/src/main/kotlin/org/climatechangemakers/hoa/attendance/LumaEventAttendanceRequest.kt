package org.climatechangemakers.hoa.attendance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class LumaEventAttendanceRequest(
  @SerialName("full_name") val fullName: String,
  val email: String,
  val status: String,
  @SerialName("event_id") val eventId: String,
  @SerialName("has_joined_event") val hasJoined: Boolean,
)