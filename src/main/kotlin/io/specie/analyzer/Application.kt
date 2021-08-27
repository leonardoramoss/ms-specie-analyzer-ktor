package io.specie.analyzer

import io.ktor.application.Application
import io.specie.analyzer.configuration.databaseConfiguration
import io.specie.analyzer.configuration.eventConfiguration
import io.specie.analyzer.configuration.koinConfiguration
import io.specie.analyzer.configuration.ktorConfiguration
import io.specie.analyzer.configuration.routingConfiguration

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    ktorConfiguration()
    koinConfiguration()
    eventConfiguration()
    databaseConfiguration()
    routingConfiguration()
}
