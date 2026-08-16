package hu.tb.meet

import hu.tb.meet.install.configureDatabase
import hu.tb.meet.install.configureKoin
import hu.tb.meet.install.configureMonitoring
import hu.tb.meet.install.configureSecurity
import hu.tb.meet.install.configureSerialization
import hu.tb.meet.install.configureStatusPages
import hu.tb.meet.install.setupRoute
import io.ktor.server.application.Application

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
