package io.specie.analyzer.application.domain.event

import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.spi.Repository
import java.util.UUID

class SpecieAnalyzedListener(
    private val repository: Repository<SpecieAnalysis, UUID>
) : EventListener<DomainEvent.SpecieAnalyzed> {

    override fun onEvent(event: DomainEvent.SpecieAnalyzed) {
        repository.save(event.getSource())
    }
}
