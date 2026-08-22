plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    alias(libs.plugins.jib)
}

group = "hu.tb"
version = "1.1.1"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

jib {
    to {
        image = "btheofil/meeting-server"
        tags = setOf(version.toString())
    }
}

ktor {
    openApi {
        enabled = true
    }
}

dependencies {
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.swagger)
    implementation(ktorLibs.server.routingOpenapi)

    implementation(libs.logback.classic)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.exposed)
    implementation(libs.sqlite)
    implementation(libs.bcrypt)

    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
