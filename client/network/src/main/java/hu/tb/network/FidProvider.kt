package hu.tb.network

fun interface FidProvider {
    suspend fun getFirebaseFid(): String?
}
