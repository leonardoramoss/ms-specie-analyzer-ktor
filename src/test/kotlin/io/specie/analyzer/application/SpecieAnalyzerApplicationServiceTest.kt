package io.specie.analyzer.application

import io.specie.analyzer.application.domain.event.EventBus
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.domain.specie.stats.StatsIdentifier
import io.specie.analyzer.application.spi.Fetcher
import org.junit.jupiter.api.DisplayName
import kotlin.test.Test
import kotlin.test.assertTrue

@DisplayName("Specie Analyzer Application")
internal class SpecieAnalyzerApplicationServiceTest {

    private val fetcher = Fetcher<SpecieAnalysis, SpecieAnalysis> { null }
    private val HORIZONTAL_SIMIAN = arrayOf("ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG")

    private val specieAnalyzerApplicationService = SpecieAnalyzerApplicationService(
        fetcher, mapOf(), mapOf(), EventBus()
    )

    @Test
    fun `given a not identified stats, when perform view stats, should be result failure`() {
        val viewStats = specieAnalyzerApplicationService.viewStats(StatsIdentifier.NOT_IDENTIFIED)
        assertTrue(viewStats.isFailure)
    }

    @Test
    fun `given a not identified species, when perform analyze, should be result failure`() {
        val specieAnalyzed = specieAnalyzerApplicationService.analyze(SpecieAnalysis(dna = HORIZONTAL_SIMIAN))
        assertTrue(specieAnalyzed.isFailure)
    }
}
