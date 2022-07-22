package org.climatechangemakers.lambda.model

import kotlinx.serialization.Serializable

@Serializable public class ApiGatewayResponseV2(
  public val statusCode: Int,
  public val headers: Map<String, String> = emptyMap(),
  public val cookies: List<String> = emptyList(),
  public val isBase64Encoded: Boolean = false,
  public val body: String? = null,
)