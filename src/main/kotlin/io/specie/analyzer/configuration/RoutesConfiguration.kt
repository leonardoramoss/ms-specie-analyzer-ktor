package io.specie.analyzer.configuration

import io.ktor.application.Application
import io.ktor.routing.routing
import io.specie.analyzer.adapters.inbound.httproutes.specieAnalysisRoutes

fun Application.routingConfiguration() {
    routing {
        specieAnalysisRoutes()
    }
}
