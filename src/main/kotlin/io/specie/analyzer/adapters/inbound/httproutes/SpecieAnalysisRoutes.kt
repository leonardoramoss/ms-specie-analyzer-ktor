package io.specie.analyzer.adapters.inbound.httproutes

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.specie.analyzer.application.SpecieAnalyzerApplicationService
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.application.domain.specie.stats.StatsIdentifier
import io.specie.analyzer.configuration.respondFailure
import org.koin.ktor.ext.get

fun Route.specieAnalysisRoutes() {

    val applicationService: SpecieAnalyzerApplicationService = get()

    route("/v1") {
        post("/simian") {
            val dnaChain: JsonNode = call.receive()

            val specieAnalysis = SpecieAnalysis(dna = dnaChain.get("dna").map { it.asText() }.toTypedArray())
                .markExpectedIdentifierAs(SpecieIdentifier.SIMIAN)

            applicationService.analyze(specieAnalysis)
                .onSuccess { call.response.status(OK) }
                .onFailure { call.respondFailure(Forbidden, it) }
        }

        get("/stats") {
            applicationService.viewStats(StatsIdentifier.RATIO_SIMIAN_HUMAN)
                .onSuccess { call.respond(OK, it) }
                .onFailure { call.respondFailure(Forbidden, it) }
        }
    }
}
