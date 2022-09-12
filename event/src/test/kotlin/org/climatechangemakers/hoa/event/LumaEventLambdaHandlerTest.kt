package org.climatechangemakers.hoa.event

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class LumaEventLambdaHandlerTest : TestContainerProvider() {

//  @Test fun `

  private fun handler(
    service: LumaService = FakeLumaService(),
    clock: Clock = FakeClock(Instant.fromEpochSeconds(0)),
  ) = LumaEventLambdaHandler(
    database = database,
    lumaService = service,
    clock = clock,
  )
}