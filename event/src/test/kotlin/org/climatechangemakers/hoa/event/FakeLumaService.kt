package org.climatechangemakers.hoa.event

class FakeLumaService : LumaService {

  override suspend fun getEvents(): List<LumaEvent> {
    TODO("Not yet implemented")
  }

  override suspend fun getGuestsForEvent(eventSecret: String): List<LumaEventGuest> {
    TODO("Not yet implemented")
  }
}