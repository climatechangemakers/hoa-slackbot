package org.climatechangemakers.hoa.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable class LumaEventGuest(
  @SerialName("event_api_id") val eventId: String,
  @SerialName("name") val fullName: String?,
  val email: String,
  @SerialName("has_joined_event") val hasJoinedEvent: Boolean,
  @SerialName("approval_status") val approvalStatus: String,
)