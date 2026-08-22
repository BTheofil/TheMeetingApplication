package hu.tb.meet.install

import hu.tb.meet.data.model.CoachTable
import hu.tb.meet.data.model.NormalTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun configureDatabase() {
    val dockerDbFile = File("app/database/meeting.db")
    val dbPath = if (dockerDbFile.parentFile?.exists() == true)
        dockerDbFile.absoluteFile
    else "build/data.db"

    Database.connect(
        url = "jdbc:sqlite:$dbPath",
        driver = "org.sqlite.JDBC"
    )

    transaction {
        SchemaUtils.create(CoachTable, NormalTable)
    }
}
