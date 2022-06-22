plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

group = "org.climatechangemakers"
version = "0.0.1"

kotlin {

  linuxX64 {
    binaries {
      executable {
        entryPoint = "org.climatechangemakers.hoa.webhook.main"
      }
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":multiplatform-aws-lambda-runtime"))
      }
    }
    val commonTest by getting
  }
}
