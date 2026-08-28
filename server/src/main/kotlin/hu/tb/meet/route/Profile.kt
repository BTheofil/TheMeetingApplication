package hu.tb.meet.route

import hu.tb.meet.data.repository.ProfileRepository
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.send.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import org.koin.ktor.ext.inject

fun Route.profile() {
    val profileRepository by inject<ProfileRepository>()

    authenticate("auth-jwt") {
        delete("/profile") {
            val payload = call.principal<JWTPrincipal>()?.payload
            val username = payload?.getClaim("username")?.asString()
            val accountType = payload?.getClaim("type")?.asString()
                ?.let { name -> AccountType.entries.find { it.name == name } }

            if (username == null || accountType == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token is not valid or has expired"))
                return@delete
            }

            if (profileRepository.deleteProfile(accountType, username) == 0) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Profile no longer exists"))
                return@delete
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}
