package hu.tb.meet.install

import hu.tb.meet.data.model.CoachTable
import hu.tb.meet.data.model.NormalTable
import hu.tb.meet.data.model.SubscriptionTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun Application.configureDatabase() {
    val dockerDbFile = File("app/database/meeting.db")
    val dbPath = if (dockerDbFile.parentFile?.exists() == true)
        dockerDbFile.absoluteFile
    else "build/data.db"

    val dbUrl = environment.config.propertyOrNull("database.url")?.getString()
        ?: "jdbc:sqlite:$dbPath"

    Database.connect(
        url = "$dbUrl?foreign_keys=on",
        driver = "org.sqlite.JDBC"
    )

    transaction {
        SchemaUtils.create(CoachTable, NormalTable, SubscriptionTable)
    }
}
