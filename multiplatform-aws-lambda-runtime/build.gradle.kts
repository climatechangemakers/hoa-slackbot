plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.serialization)
}

group = "org.climatechangemakers"
version = "0.0.1"

kotlin {

  explicitApi()

  jvm()
  linuxX64()

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(libs.kotlinx.coroutines)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.cio)
        implementation(libs.kotlinx.serialization.json)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.turbine)
      }
    }
  }
}