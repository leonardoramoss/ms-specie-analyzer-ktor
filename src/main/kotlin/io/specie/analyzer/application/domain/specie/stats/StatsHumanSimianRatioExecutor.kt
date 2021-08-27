package io.specie.analyzer.application.domain.specie.stats

import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.application.spi.Fetcher
import java.math.BigDecimal
import java.math.RoundingMode

class StatsHumanSimianRatioExecutor(
    private val fetcher: Fetcher<List<SpecieIdentifier>, Map<SpecieIdentifier, Long>>
) : StatsExecutor<Map<String, Any>> {

    override fun execute(): Map<String, Any> {
        val speciesCount = fetcher.fetch(listOf(SpecieIdentifier.HUMAN, SpecieIdentifier.SIMIAN))

        val humanCount = speciesCount?.get(SpecieIdentifier.HUMAN) ?: 0
        val simianCount = speciesCount?.get(SpecieIdentifier.SIMIAN) ?: 0

        val ratio = if (humanCount == 0L) {
            BigDecimal.valueOf(simianCount)
        } else {
            BigDecimal.valueOf((simianCount * 100.0f / humanCount).toDouble())
                .divide(BigDecimal.valueOf(100), 1, RoundingMode.HALF_UP)
        }

        return mapOf(
            "count_mutant_dna" to simianCount,
            "count_human_dna" to humanCount,
            "ratio" to ratio
        )
    }
}
