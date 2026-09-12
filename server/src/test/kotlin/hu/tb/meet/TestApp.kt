package hu.tb.meet

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.io.File

val testConfig = ApplicationConfig("application-test.conf")

val testJwtIssuer = testConfig.property("jwt.issuer").getString()
val testJwtAudience = testConfig.property("jwt.audience").getString()
val testJwtSecret = testConfig.propertyOrNull("jwt.secret.meeting")?.getString() ?: "debug_build"

fun withTestApp(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) = testApplication {
    resetTestDatabase()

    environment {
        config = testConfig
    }

    val client = createClient {
        install(ContentNegotiation) { json() }
    }

    block(client)
}

private fun resetTestDatabase() {
    val path = testConfig.property("database.url").getString()
        .substringAfter("jdbc:sqlite:")
        .substringBefore("?")
    val dbFile = File(path)
    dbFile.parentFile?.mkdirs()
    // -wal / -shm are SQLite's journal side files; leaving them behind leaks state
    listOf(dbFile, File("$path-wal"), File("$path-shm")).forEach { it.delete() }
}
