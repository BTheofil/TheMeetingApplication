package hu.tb.meet.route

import at.favre.lib.crypto.bcrypt.BCrypt
import hu.tb.meet.data.repository.AuthRepository
import hu.tb.meet.domain.receive.AuthReceive
import hu.tb.meet.domain.send.AuthResponse
import hu.tb.meet.domain.send.ErrorResponse
import hu.tb.meet.security.JwtService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject

fun Route.auth() {

    val authRepository by inject<AuthRepository>()
    val jwtService by inject<JwtService>()

    post("/login") {
        val loginInfo = call.receive<AuthReceive>()
        val account = authRepository.find(loginInfo.type, loginInfo.username)

        val verified = account != null &&
                BCrypt.verifyer().verify(loginInfo.password.toCharArray(), account.passwordHash).verified

        if (!verified) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid username or password"))
            return@post
        }

        val token = jwtService.generate(loginInfo.username, loginInfo.type)
        call.respond(HttpStatusCode.OK, AuthResponse(token))
    }

    post("/register") {
        val registerInfo = call.receive<AuthReceive>()

        if (authRepository.exists(registerInfo.type, registerInfo.username)) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse("Username already taken"))
            return@post
        }

        val hash = BCrypt.withDefaults().hashToString(12, registerInfo.password.toCharArray())
        authRepository.create(registerInfo.type, registerInfo.username, hash)

        val token = jwtService.generate(registerInfo.username, registerInfo.type)
        call.respond(HttpStatusCode.Created, AuthResponse(token))
    }
}
