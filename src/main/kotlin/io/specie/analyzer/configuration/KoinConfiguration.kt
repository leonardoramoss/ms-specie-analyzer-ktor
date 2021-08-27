package io.specie.analyzer.configuration

import io.ktor.application.Application
import io.ktor.application.install
import io.specie.analyzer.adapters.outbound.fetcher.SpecieAnalyzedFetcher
import io.specie.analyzer.adapters.outbound.fetcher.StatsCounterHumanSimianFetcher
import io.specie.analyzer.adapters.outbound.persistence.exposed.repository.SpecieAnalisysRepository
import io.specie.analyzer.application.SpecieAnalyzerApplicationService
import io.specie.analyzer.application.domain.event.EventBus
import io.specie.analyzer.application.domain.event.EventNotifier
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.application.domain.specie.analyzer.Analyzer
import io.specie.analyzer.application.domain.specie.analyzer.PrimateAnalyzer
import io.specie.analyzer.application.domain.specie.stats.StatsExecutor
import io.specie.analyzer.application.domain.specie.stats.StatsHumanSimianRatioExecutor
import io.specie.analyzer.application.domain.specie.stats.StatsIdentifier
import io.specie.analyzer.application.spi.Fetcher
import io.specie.analyzer.application.spi.Repository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

fun Application.koinConfiguration() {

    install(Koin) {

        val namedAnalyzer = "analyzer"

        modules(
            module {

                single { EventBus() } bind EventNotifier::class

                single { SpecieAnalisysRepository() } bind Repository::class

                single<Fetcher<SpecieAnalysis, SpecieAnalysis>>(named(namedAnalyzer)) { SpecieAnalyzedFetcher(get()) }

                single<Map<SpecieIdentifier, Analyzer>>(named(namedAnalyzer)) {
                    HashMap<SpecieIdentifier, Analyzer>().apply {
                        this[SpecieIdentifier.SIMIAN] = PrimateAnalyzer()
                    }
                }

                single<Fetcher<List<SpecieIdentifier>, Map<SpecieIdentifier, Long>>> { StatsCounterHumanSimianFetcher() }

                single<Map<StatsIdentifier, StatsExecutor<*>>> {
                    HashMap<StatsIdentifier, StatsExecutor<*>>().apply {
                        this[StatsIdentifier.RATIO_SIMIAN_HUMAN] = StatsHumanSimianRatioExecutor(get())
                    }
                }

                single {
                    SpecieAnalyzerApplicationService(
                        get(named(namedAnalyzer)),
                        get(named(namedAnalyzer)),
                        get(),
                        get()
                    )
                }
            }
        )
    }
}
