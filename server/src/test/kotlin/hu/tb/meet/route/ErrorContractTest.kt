package hu.tb.meet.route

import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.withTestApp
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorContractTest {

    @Test
    fun `an unknown route answers with the page not found error`() = withTestApp { client ->
        val response = client.get("/nope")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Page not found", response.body<ErrorResponse>().message)
    }
}
