package org.climatechangemakers.lambda.model

import kotlinx.serialization.Serializable

@Serializable public class ApiGatewayResponseV2(
  public val statusCode: Int,
  public val headers: Map<String, String>,
  public val cookies: List<String>,
  public val isBase64Encoded: Boolean,
  public val body: String?,
)