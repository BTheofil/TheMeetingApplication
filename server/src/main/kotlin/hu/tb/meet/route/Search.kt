package hu.tb.meet.route

import hu.tb.meet.data.repository.SearchRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.SearchReceive
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.domain.send.SearchResultResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.search() {

    val searchRepository by inject<SearchRepository>()

    authenticate("auth-jwt") {
        post("/searchCoach") {
            val payload = call.principal<JWTPrincipal>()?.payload
            val username = payload?.getClaim("username")?.asString()
            val accountType = payload?.getClaim("type")?.asString()
                ?.let { name -> AccountType.entries.find { it.name == name } }

            if (username == null || accountType != AccountType.NORMAL) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token is not valid or has expired"))
                return@post
            }

            val searchReceive = call.receive<SearchReceive>()

            val coaches = searchRepository.searchCoach(username, searchReceive.searchCoachName)

            call.respond(HttpStatusCode.OK, SearchResultResponse(coaches = coaches))
        }
    }
}
