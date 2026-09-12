package hu.tb.network

fun interface TokenProvider {
    suspend fun getTokenOrNull(): String?
}
