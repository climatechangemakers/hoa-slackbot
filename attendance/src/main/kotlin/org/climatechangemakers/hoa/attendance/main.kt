@file:JvmName("Main")

package org.climatechangemakers.hoa.attendance

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.climatechangemakers.hoa.attendance.database.Database
import org.climatechangemakers.hoa.attendance.database.HourOfActionEventAttendanceQueries
import org.climatechangemakers.lambda.runtime.runLambda
import org.postgresql.ds.PGSimpleDataSource

suspend fun main() {
  val driver = PGSimpleDataSource().apply {
    serverNames = arrayOf(getEnvironmentVariable(EnvironmentVariable.DatabaseHostname))
    portNumbers = intArrayOf(getEnvironmentVariable(EnvironmentVariable.DatabasePort).toInt())
    user = getEnvironmentVariable(EnvironmentVariable.DatabaseUser)
    password = getEnvironmentVariable(EnvironmentVariable.DatabasePassword)
    databaseName = getEnvironmentVariable(EnvironmentVariable.DatabaseName)
  }.asJdbcDriver()
  runLambda(LumaEventAttendanceLambdaHandler(Database(driver)))
}