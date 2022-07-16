package org.climatechangemakers.lambda.model

import kotlinx.serialization.Serializable

// TODO(kcianfarini) Remove data modifier for binary and source compatability.

@Serializable public data class ApiGatewayRequestV2(
  public val version: String,
  public val routeKey: String,
  public val rawPath: String,
  public val rawQueryString: String,
  public val cookies: List<String> = emptyList(),
  public val headers: Map<String, String>,
  public val queryStringParameters: Map<String, String> = emptyMap(),
  public val requestContext: RequestContext,
  public val body: String?,
  public val pathParameters: Map<String, String> = emptyMap(),
  public val isBase64Encoded: Boolean,
  public val stageVariables: Map<String, String> = emptyMap(),
) {

  @Serializable public data class RequestContext(
    public val accountId: String,
    public val apiId: String,
    public val authentication: Authentication? = null,
    public val authorizer: Authorizer? = null,
    public val domainName: String,
    public val domainPrefix: String,
    public val http: HttpContext,
    public val requestId: String,
    public val routeKey: String,
    public val stage: String,
    public val time: String,
    public val timeEpoch: Long,
  ) {

    @Serializable public data class Authentication(public val clientCert: ClientCertification) {

      @Serializable public data class ClientCertification(
        public val clientCertPem: String,
        public val subjectDN: String,
        public val issuerDN: String,
        public val serialNumber: String,
        public val validity: Validity,
      ) {

        @Serializable public data class Validity(
          public val notBefore: String,
          public val notAfter: String,
        )
      }
    }

    @Serializable public data class Authorizer(public val jwt: Jwt) {

      @Serializable public data class Jwt(
        public val claims: Map<String, String>,
        public val scopes: List<String>,
      )
    }

    @Serializable public data class HttpContext(
      public val method: String,
      public val path: String,
      public val protocol: String,
      public val sourceIp: String,
      public val userAgent: String,
    )
  }
}