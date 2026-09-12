package hu.tb.meet

import hu.tb.meet.install.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureStatusPages()
    configureDatabase()
    configureKoin()
    configureSecurity()
    setupRoute()
}
