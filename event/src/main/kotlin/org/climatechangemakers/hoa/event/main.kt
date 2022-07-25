package org.climatechangemakers.hoa.event

import org.climatechangemakers.lambda.runtime.runLambda

suspend fun main() = runLambda(
  LumaEventLambdaHandler(TODO())
)
