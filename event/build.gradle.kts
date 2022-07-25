plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.sqldelight)
}

group = "org.climatechangemakers"
version = "0.0.1"

sqldelight {
  database("Database") {
    packageName = "org.climatechangemakers.hoa.event.database"
    dialect(libs.sqldelight.postgresql.dialect.get().toString())
    deriveSchemaFromMigrations = false
    verifyMigrations = false
  }
}

dependencies {
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.postgresql)
  implementation(libs.sqldelight.jdbc.driver)
  implementation(project(":multiplatform-aws-lambda-runtime"))

  testImplementation(libs.kotlin.test)
  testImplementation(libs.testcontainers.postgresql)
}