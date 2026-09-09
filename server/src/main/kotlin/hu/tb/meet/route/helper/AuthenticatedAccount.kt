package hu.tb.meet.route.helper

import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.AccountType
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingContext

data class AuthenticatedAccount(val username: String, val type: AccountType)

fun RoutingContext.requireAccount(vararg allowed: AccountType): AuthenticatedAccount {
    val payload = call.principal<JWTPrincipal>()?.payload ?: fail(ApiError.INVALID_TOKEN)
    val username = payload.getClaim("username").asString() ?: fail(ApiError.INVALID_TOKEN)
    val type = payload.getClaim("type").asString()
        ?.let { name -> AccountType.entries.find { it.name == name } }
        ?: fail(ApiError.INVALID_TOKEN)

    if (allowed.isNotEmpty() && type !in allowed) {
        fail(ApiError.WRONG_ACCOUNT_TYPE)
    }

    return AuthenticatedAccount(username, type)
}
