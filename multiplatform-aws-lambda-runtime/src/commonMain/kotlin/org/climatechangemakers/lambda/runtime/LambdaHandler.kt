package org.climatechangemakers.lambda.runtime

import org.climatechangemakers.lambda.model.ApiGatewayRequestV2
import org.climatechangemakers.lambda.model.ApiGatewayResponseV2
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse

public fun interface LambdaHandler<Request : Any, Response : Any> {

  public suspend fun invoke(request: Request): Response
}

public fun interface RawLambdaHandler : LambdaHandler<RawRequest, RawResponse>

public fun interface ApiGatewayV2LambdaHandler : LambdaHandler<ApiGatewayRequestV2, ApiGatewayResponseV2>
