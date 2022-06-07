plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

group = "org.climatechangemakers"
version = "0.0.1"

kotlin {

  jvm()
  linuxX64()

  sourceSets {
    val commonMain by getting {
      dependencies {

      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test)
      }
    }
  }
}