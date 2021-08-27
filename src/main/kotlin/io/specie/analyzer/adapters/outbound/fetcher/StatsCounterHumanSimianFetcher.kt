package io.specie.analyzer.adapters.outbound.fetcher

import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisCounterEntity
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisCounterEntity.counter
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisCounterEntity.specie
import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import io.specie.analyzer.application.spi.Fetcher
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class StatsCounterHumanSimianFetcher : Fetcher<List<SpecieIdentifier>, Map<SpecieIdentifier, Long>> {
    override fun fetch(argument: List<SpecieIdentifier>?): Map<SpecieIdentifier, Long>? {
        return transaction {
            SpecieAnalysisCounterEntity
                .select { specie inList argument!! }
                .associate { it[specie] to it[counter] }
        }
    }
}
