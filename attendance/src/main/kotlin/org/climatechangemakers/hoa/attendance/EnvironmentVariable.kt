package org.climatechangemakers.hoa.attendance

enum class EnvironmentVariable(val key: String) {
  DatabasePassword("POSTGRES_PASSWORD"),
  DatabaseUser("POSTGRES_USER"),
  DatabaseName("POSTGRES_DB"),
  DatabaseHostname("POSTGRES_HOSTNAME"),
  DatabasePort("POSTGRES_PORT"),
}

fun getEnvironmentVariable(
  key: EnvironmentVariable,
): String = requireNotNull(System.getenv(key.key)) { "No environment variable ${key.key} set" }
