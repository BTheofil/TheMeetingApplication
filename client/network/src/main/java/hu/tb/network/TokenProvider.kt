package hu.tb.network

fun interface TokenProvider {
    suspend fun token(): String?
}
