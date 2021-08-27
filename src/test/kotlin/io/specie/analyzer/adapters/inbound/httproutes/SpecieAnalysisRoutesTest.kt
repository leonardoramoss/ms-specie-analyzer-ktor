package io.specie.analyzer.adapters.inbound.httproutes

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.application.Application
import io.ktor.http.HttpStatusCode
import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.configuration.IntegrationTest
import io.specie.analyzer.configuration.JsonFixture.Companion.loadJsonFile
import io.specie.analyzer.configuration.JsonFixture.Companion.loadJsonFileAsJsonNode
import io.specie.analyzer.configuration.SpecieAnalysisDataProvider
import io.specie.analyzer.configuration.SpecieAnalysisDataProvider.Companion.defaultIgnoredColumns
import io.specie.analyzer.configuration.SpecieAnalysisDataProvider.Companion.specieAnalysisCounterFromClause
import io.specie.analyzer.configuration.SpecieAnalysisDataProvider.Companion.specieAnalysisFromClause
import io.specie.analyzer.module
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName

@DisplayName("Specie Analysis Routes")
internal class SpecieAnalysisRoutesTest : IntegrationTest(Application::module) {

    @BeforeTest
    fun beforeEach() {
        executeScript("scripts/clear.sql")
    }

    @ArgumentsSource(SpecieAnalysisDataProvider::class)
    @ParameterizedTest(name = "#{index} - Parameters: {0}")
    fun `given test data, should be match dataset and response status`(scenario: SpecieAnalysisDataProvider.Scenario) {
        post("/v1/simian", scenario.requestBody) {
            assertAll(
                { assertEquals(scenario.responseCode, status()) },
                {
                    scenario.assertionDatabase.forEach {
                        assertDatabase(it.fromClause, it.assertionDataset, *it.ignoredColumns.toTypedArray())
                    }
                }
            )
        }
        Thread.sleep(100)
    }

    @Test
    fun `given a valid simian DNA, when perform post and already saved DNA, should be return from database and status OK`() {
        repeat(2) {
            post("/v1/simian", loadJsonFile("mock/mock_simian_horizontal_payload.json")) {
                assertAll(
                    { assertEquals(HttpStatusCode.OK, status()) },
                    { assertDatabase(specieAnalysisFromClause, "expected_simian_horizontal_specie.xml", *defaultIgnoredColumns.toTypedArray()) },
                    { assertDatabase(specieAnalysisCounterFromClause, "expected_simian_counter.xml") }
                )
            }
        }
    }

    @Test
    fun `given a invalid payload with null DNA, when perform POST, should be return status BadRequest`() {
        post("/v1/simian", "{ \"dna\": }") {
            assertAll(
                { assertEquals(HttpStatusCode.BadRequest, status()) },
                { assertDatabase(specieAnalysisFromClause, "expected_invalid_specie.xml") },
                { assertDatabase(specieAnalysisCounterFromClause, "expected_empty_simian_counter.xml") }
            )
        }
    }

    @Test
    fun `given batch of valid requests, when perform post, should be return status OK for simians and Forbidden for Human and stats`() {

        // Given
        val expectations = mapOf(
            SpecieIdentifier.HUMAN to HttpStatusCode.Forbidden,
            SpecieIdentifier.SIMIAN to HttpStatusCode.OK
        )

        val payloads = getPayloads(
            "mock/mock_simian_payloads.json",
            "mock/mock_human_payloads.json"
        )

        // When
        payloads.parallelStream().forEach {
            val speciesIdentifier = SpecieIdentifier.valueOf(it.get("species").asText())
            post("/v1/simian", it.get("payload").toString()) { assertEquals(expectations[speciesIdentifier]!!, status()) }
        }

        // Then
        await().pollInterval(100, TimeUnit.MILLISECONDS)
            .timeout(5000, TimeUnit.MILLISECONDS)
            .until({ executeCount(specieAnalysisFromClause) }, Matchers.equalTo(150))

        // When
        get("/v1/stats") {
            val querySpeciesAnalysis = "$specieAnalysisFromClause ORDER BY UUID"
            val querySpeciesAnalysisCounter = "$specieAnalysisCounterFromClause ORDER BY SPECIE"

            assertAll(
                { assertEquals(loadJsonFile("expected/response/stats/fullflow/expected_fullflow_stats.json"), content, false) },
                { assertDatabase(querySpeciesAnalysis, "fullflow/expected_valid_human_and_simian_payloads.xml", *defaultIgnoredColumns.toTypedArray()) },
                { assertDatabase(querySpeciesAnalysisCounter, "fullflow/expected_species_counter.xml") }
            )
        }
    }

    @Test
    fun `given previous species analyzed, when perform get stats, should be return ratio stats and status OK`() {
        executeScript("scripts/data.sql")

        get("/v1/stats") {
            assertAll(
                { assertEquals(HttpStatusCode.OK, status()) },
                { assertEquals(loadJsonFile("expected/response/stats/expected_stats.json"), content, true) }
            )
        }
    }

    @Test
    fun `given none species analyzed, when perform get stats, should be return ratio zero stats and status OK`() {
        get("/v1/stats") {
            assertAll(
                { assertEquals(HttpStatusCode.OK, status()) },
                { assertEquals(loadJsonFile("expected/response/stats/expected_zero_stats.json"), content, true) }
            )
        }
    }

    private fun getPayloads(vararg pathsMockPayloads: String): List<JsonNode> =
        pathsMockPayloads.map { loadJsonFileAsJsonNode(it) }.flatMap { it.toList() }
}
