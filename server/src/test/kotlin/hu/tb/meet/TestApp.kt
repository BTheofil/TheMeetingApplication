package hu.tb.meet

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.io.File

private const val TEST_DB_DIR = "build/test-db"

fun withTestApp(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) = testApplication {
    resetTestDatabase()

    environment {
        config = ApplicationConfig("application-test.conf")
    }

    val client = createClient {
        install(ContentNegotiation) { json() }
    }

    block(client)
}

private fun resetTestDatabase() {
    val dir = File(TEST_DB_DIR)
    dir.mkdirs()
    // -wal / -shm are SQLite's journal side files; leaving them behind leaks state
    dir.listFiles()?.forEach { it.delete() }
}
