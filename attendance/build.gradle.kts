plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotlin.serialization)
}

group = "org.climatechangemakers"
version = "0.0.1"

kotlin {

  linuxX64 {
    binaries {
      executable {
        entryPoint = "org.climatechangemakers.hoa.attendance.main"
      }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
        implementation(project(":multiplatform-aws-lambda-runtime"))
      }
    }
    val commonTest by getting
  }
}
