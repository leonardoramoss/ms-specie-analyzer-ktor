package io.specie.analyzer.configuration

import io.ktor.http.HttpStatusCode
import io.specie.analyzer.configuration.JsonFixture.Companion.loadJsonFile
import java.util.stream.Stream
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider

class SpecieAnalysisDataProvider : ArgumentsProvider {

    companion object {
        const val specieAnalysisFromClause = "SPECIE.SPECIES_ANALYSIS"
        const val specieAnalysisCounterFromClause = "SPECIE.SPECIES_ANALYSIS_COUNTER"
        val defaultIgnoredColumns = listOf("ANALYZED_AT")
    }

    private val invalidSimian = DatabaseState(specieAnalysisFromClause, "expected_invalid_specie.xml")
    private val simianCounter = DatabaseState(specieAnalysisCounterFromClause, "expected_simian_counter.xml")
    private val humanCounter = DatabaseState(specieAnalysisCounterFromClause, "expected_human_counter.xml")
    private val emptySimianCounter = DatabaseState(specieAnalysisCounterFromClause, "expected_empty_simian_counter.xml")

    override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
        return Stream.of(
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/mock_simian_horizontal_payload.json"),
                    HttpStatusCode.OK,
                    listOf(
                        DatabaseState(specieAnalysisFromClause, "expected_simian_horizontal_specie.xml", defaultIgnoredColumns),
                        simianCounter
                    )
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/mock_simian_vertical_payload.json"),
                    HttpStatusCode.OK,
                    listOf(
                        DatabaseState(specieAnalysisFromClause, "expected_simian_vertical_species.xml", defaultIgnoredColumns),
                        simianCounter
                    )
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/mock_simian_diagonal_payload.json"),
                    HttpStatusCode.OK,
                    listOf(
                        DatabaseState(specieAnalysisFromClause, "expected_simian_diagonal_species.xml", defaultIgnoredColumns),
                        simianCounter
                    )
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/mock_simian_reversed_diagonal_payload.json"),
                    HttpStatusCode.OK,
                    listOf(
                        DatabaseState(specieAnalysisFromClause, "expected_simian_reversed_diagonal_species.xml", defaultIgnoredColumns),
                        simianCounter
                    )
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/mock_human_payload.json"),
                    HttpStatusCode.Forbidden,
                    listOf(
                        DatabaseState(specieAnalysisFromClause, "expected_human_specie.xml", defaultIgnoredColumns),
                        humanCounter
                    )
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/invalid/mock_invalid_not_allowed_character_payload.json"),
                    HttpStatusCode.BadRequest,
                    listOf(invalidSimian, emptySimianCounter)
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/invalid/mock_invalid_empty_dna_payload.json"),
                    HttpStatusCode.BadRequest,
                    listOf(invalidSimian, emptySimianCounter)
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/invalid/mock_invalid_without_dna_label_payload.json"),
                    HttpStatusCode.BadRequest,
                    listOf(invalidSimian, emptySimianCounter)
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/invalid/mock_invalid_not_a_NxN_payload.json"),
                    HttpStatusCode.BadRequest,
                    listOf(invalidSimian, emptySimianCounter)
                )
            ),
            Arguments.of(
                Scenario(
                    loadJsonFile("mock/invalid/mock_invalid_with_less_NxN_allowed_payload.json"),
                    HttpStatusCode.BadRequest,
                    listOf(invalidSimian, emptySimianCounter)
                )
            )
        )
    }

    data class DatabaseState(
        val fromClause: String,
        val assertionDataset: String,
        val ignoredColumns: List<String> = listOf()
    )

    data class Scenario(
        val requestBody: String,
        val responseCode: HttpStatusCode,
        val assertionDatabase: Collection<DatabaseState>
    ) {
        override fun toString(): String {
            return "request body = $requestBody, expected status: $responseCode, assertion database = $assertionDatabase"
        }
    }
}
