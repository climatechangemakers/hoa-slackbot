package org.climatechangemakers.hoa.attendance

import app.cash.sqldelight.Query
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import java.sql.Connection
import java.sql.DriverManager
import org.climatechangemakers.hoa.attendance.database.Database
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

abstract class TestContainerProvider {

  // the jdbc url is special and instructs testcontainers to use the postgres 12.5 image
  private val connection = DriverManager.getConnection("jdbc:tc:postgresql:12.5:///my_db")
  protected val driver = object : JdbcDriver() {
    override fun closeConnection(connection: Connection) = Unit
    override fun getConnection(): Connection = connection
    override fun notifyListeners(queryKeys: Array<String>) = Unit
    override fun removeListener(listener: Query.Listener, queryKeys: Array<String>) = Unit
    override fun addListener(listener: Query.Listener, queryKeys: Array<String>) = Unit
  }

  protected val database = Database(driver = driver)

  @BeforeTest fun before() {
    Database.Schema.create(driver)
  }

  @AfterTest fun after() {
    connection.close()
  }
}