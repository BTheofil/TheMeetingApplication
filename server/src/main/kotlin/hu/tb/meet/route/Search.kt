package hu.tb.meet.route

import hu.tb.meet.data.repository.SearchRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.SearchReceive
import hu.tb.meet.domain.send.SearchResultResponse
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.search() {

    val searchRepository by inject<SearchRepository>()

    authenticate("auth-jwt") {
        post("/searchCoach") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val searchReceive = call.receive<SearchReceive>()

            val coaches = searchRepository.searchCoach(username, searchReceive.searchCoachName)

            call.respond(HttpStatusCode.OK, SearchResultResponse(coaches = coaches))
        }
    }
}
