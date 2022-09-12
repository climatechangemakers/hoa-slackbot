package org.climatechangemakers.hoa.webhook

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeClock(private val now: Instant) : Clock {
  override fun now(): Instant = now
}