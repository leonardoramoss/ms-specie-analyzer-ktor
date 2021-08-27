package io.specie.analyzer.adapters.outbound.fetcher

import io.specie.analyzer.adapters.outbound.persistence.exposed.repository.SpecieAnalisysRepository
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.spi.Fetcher
import java.util.UUID

class SpecieAnalyzedFetcher(
    private val specieAnalysisRepository: SpecieAnalisysRepository
) : Fetcher<SpecieAnalysis, SpecieAnalysis> {

    override fun fetch(argument: SpecieAnalysis?): SpecieAnalysis? =
        specieAnalysisRepository.findById(UUID.nameUUIDFromBytes(argument?.dna?.toByteArray()))
}
