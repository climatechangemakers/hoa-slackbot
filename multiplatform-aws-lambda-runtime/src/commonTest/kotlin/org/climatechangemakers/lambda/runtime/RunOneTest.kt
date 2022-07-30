package org.climatechangemakers.lambda.runtime

import app.cash.turbine.test
import org.climatechangemakers.lambda.fake.FakeAwsService
import org.climatechangemakers.lambda.model.RawRequest
import org.climatechangemakers.lambda.model.RawResponse
import org.climatechangemakers.lambda.runWithTimeout
import org.climatechangemakers.lambda.service.AwsService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RunOneTest {

  private val fakeAwsService = FakeAwsService()

  @Test fun `successful invocation reports response to correct id`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("some_id", "") }
    fakeAwsService.rawResponses.test {
      runOnce(fakeAwsService) { RawResponse("This is a response, yo!") }
      assertEquals(
        expected = "some_id",
        actual = awaitItem().first,
      )
    }
  }

  @Test fun `successful invocation reports correct response`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("", "") }
    fakeAwsService.rawResponses.test {
      val response = RawResponse("This is a response, yo!")
      runOnce(fakeAwsService) { response }
      assertSame(
        expected = response,
        actual = awaitItem().second,
      )
    }
  }

  @Test fun `exception in lambda handler does not report reponse`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.rawResponses.test {
      runOnce(fakeAwsService) { throw LambdaHandlerException() }
      expectNoEvents()
    }
  }

  @Test fun `exception recieving event reports init error`() = runWithTimeout {
    fakeAwsService.queueNext { throw Exception("Poopy butt.") }
    fakeAwsService.initializationErrors.test {
      runOnce(fakeAwsService) { TODO() }
      assertEquals(
        expected = "Poopy butt.",
        actual = awaitItem().message,
      )
    }
  }

  @Test fun `exception submitting response reports init error`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.initializationErrors.test {
      runOnce(fakeAwsService) { RawResponse("") }
      assertEquals(
        expected = "Poopy butt.",
        actual = awaitItem().message,
      )
    }
  }

  @Test fun `explicit lambda handler exception reports invocation error`() = runWithTimeout {
    fakeAwsService.queueNext {
      RawRequest("", "")
    }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.invocationExceptions.test {
      val exception = LambdaHandlerException()
      runOnce(fakeAwsService) { throw exception }
      assertSame(
        expected = exception,
        actual = awaitItem().second,
      )
    }
  }

  @Test fun `explicit lambda handler exception reports for correct request id`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("some_id", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.invocationExceptions.test {
      runOnce(fakeAwsService) { throw LambdaHandlerException() }
      assertEquals(
        expected = "some_id",
        actual = awaitItem().first
      )
    }
  }

  @Test fun `uncaught lambda handler exception reports invocation error`() = runWithTimeout {
    fakeAwsService.queueNext { RawRequest("", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.invocationExceptions.test {
      val exception = IllegalArgumentException()
      runOnce(fakeAwsService) { throw exception }
      assertSame(
        expected = exception,
        actual = awaitItem().second.cause,
      )
    }
  }
}

private suspend fun runOnce(service: AwsService, function: (RawRequest) -> RawResponse) {
  runOnce(service, object : RawLambdaHandler {
    override suspend fun invoke(request: RawRequest): RawResponse {
      return function(request)
    }
  })
}