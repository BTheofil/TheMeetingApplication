package hu.tb.meet.install

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

private const val SQLITE_URL_PREFIX = "jdbc:sqlite:"

fun Application.configureDatabase() {
    val url = environment.config.property("database.url").getString()

    // SQLite will not create missing parent directories for the database file.
    if (url.startsWith(SQLITE_URL_PREFIX)) {
        File(url.removePrefix(SQLITE_URL_PREFIX)).absoluteFile.parentFile?.mkdirs()
    }

    Database.connect(url = url, driver = "org.sqlite.JDBC")

    transaction {
        // Register tables here as the schema grows:
        // SchemaUtils.create(Meetings, Participants)
        log.info("Database connected: $url")
    }
}
