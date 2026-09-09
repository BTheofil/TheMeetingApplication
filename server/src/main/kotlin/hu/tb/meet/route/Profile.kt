package hu.tb.meet.route

import hu.tb.meet.data.repository.ProfileRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import org.koin.ktor.ext.inject

fun Route.profile() {
    val profileRepository by inject<ProfileRepository>()

    authenticate("auth-jwt") {
        delete("/profile") {
            val account = requireAccount()

            if (profileRepository.deleteProfile(account.type, account.username) == 0) {
                fail(ApiError.PROFILE_NOT_FOUND)
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}
