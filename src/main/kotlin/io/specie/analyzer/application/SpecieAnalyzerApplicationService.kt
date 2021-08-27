package io.specie.analyzer.application

import io.specie.analyzer.application.domain.event.DomainEvent
import io.specie.analyzer.application.domain.event.EventNotifier
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.application.domain.specie.analyzer.Analyzer
import io.specie.analyzer.application.domain.specie.stats.StatsExecutor
import io.specie.analyzer.application.domain.specie.stats.StatsIdentifier
import io.specie.analyzer.application.spi.Fetcher

class SpecieAnalyzerApplicationService(
    private val fetchSpecieAnalyzed: Fetcher<SpecieAnalysis, SpecieAnalysis>,
    private val analyzers: Map<SpecieIdentifier, Analyzer>,
    private val executors: Map<StatsIdentifier, StatsExecutor<*>>,
    private val eventNotifier: EventNotifier
) {

    fun analyze(specieAnalysis: SpecieAnalysis): Result<SpecieAnalysis> {

        val specieAnalyzed = fetchSpecieAnalyzed.fetch(specieAnalysis) ?: let {

            val analyzer = analyzers[specieAnalysis.expectedIdentifier]
                ?: return Result.failure(IllegalStateException("There are no analyzer for this specie: ${specieAnalysis.expectedIdentifier}"))

            analyzer.analyze(specieAnalysis).also {
                eventNotifier.notify(DomainEvent.SpecieAnalyzed(it))
            }
        }

        if (specieAnalyzed.isIdentifierMatchesAsExpected().not()) {
            return Result.failure(IllegalStateException("DNA is not from a simian"))
        }

        return Result.success(specieAnalyzed)
    }

    fun viewStats(statsIdentifier: StatsIdentifier): Result<Any> {
        return executors[statsIdentifier]?.execute()
            ?.let { Result.success(it) }
            ?: let { Result.failure(IllegalStateException("There are no stats executor configured for: ${statsIdentifier.name}")) }
    }
}
