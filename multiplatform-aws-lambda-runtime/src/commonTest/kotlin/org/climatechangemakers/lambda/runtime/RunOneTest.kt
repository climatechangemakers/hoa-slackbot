package org.climatechangemakers.lambda.runtime

import app.cash.turbine.test
import org.climatechangemakers.lambda.fake.FakeAwsService
import org.climatechangemakers.lambda.model.InvocationRequest
import org.climatechangemakers.lambda.model.InvocationResponse
import org.climatechangemakers.lambda.runWithTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RunOneTest {

  private val fakeAwsService = FakeAwsService()

  @Test fun `successful invocation reports response to correct id`() = runWithTimeout {
    fakeAwsService.queueNext { InvocationRequest("some_id", "") }
    fakeAwsService.invocationResponses.test {
      runOnce(fakeAwsService) { InvocationResponse("This is a response, yo!") }
      assertEquals(
        expected = "some_id",
        actual = awaitItem().first,
      )
    }
  }

  @Test fun `successful invocation reports correct response`() = runWithTimeout {
    fakeAwsService.queueNext { InvocationRequest("", "") }
    fakeAwsService.invocationResponses.test {
      val response = InvocationResponse("This is a response, yo!")
      runOnce(fakeAwsService) { response }
      assertSame(
        expected = response,
        actual = awaitItem().second,
      )
    }
  }

  @Test fun `exception in lambda handler does not report reponse`() = runWithTimeout {
    fakeAwsService.queueNext { InvocationRequest("", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.invocationResponses.test {
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
    fakeAwsService.queueNext { InvocationRequest("", "") }
    fakeAwsService.queueResponseError(Exception("Poopy butt."))
    fakeAwsService.initializationErrors.test {
      runOnce(fakeAwsService) { InvocationResponse("") }
      assertEquals(
        expected = "Poopy butt.",
        actual = awaitItem().message,
      )
    }
  }

  @Test fun `explicit lambda handler exception reports invocation error`() = runWithTimeout {
    fakeAwsService.queueNext {
      InvocationRequest("", "")
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
    fakeAwsService.queueNext { InvocationRequest("some_id", "") }
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
    fakeAwsService.queueNext { InvocationRequest("", "") }
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