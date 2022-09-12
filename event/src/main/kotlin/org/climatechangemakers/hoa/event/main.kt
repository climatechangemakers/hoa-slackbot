@file:JvmName("Main")

package org.climatechangemakers.hoa.event

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.climatechangemakers.lambda.runtime.runLambda
import org.postgresql.ds.PGSimpleDataSource
import org.climatechangemakers.hoa.event.database.Database

suspend fun main() {
  val driver = PGSimpleDataSource().apply {
    serverNames = arrayOf(getEnvironmentVariable(EnvironmentVariable.DatabaseHostname))
    portNumbers = intArrayOf(getEnvironmentVariable(EnvironmentVariable.DatabasePort).toInt())
    user = getEnvironmentVariable(EnvironmentVariable.DatabaseUser)
    password = getEnvironmentVariable(EnvironmentVariable.DatabasePassword)
    databaseName = getEnvironmentVariable(EnvironmentVariable.DatabaseName)
  }.asJdbcDriver()

  val json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
  }

  val lumaService = KtorLumaService(HttpClient(CIO), json, getEnvironmentVariable(EnvironmentVariable.LumaApiKey))
  runLambda(LumaEventLambdaHandler(Database(driver), lumaService, Clock.System))
}
