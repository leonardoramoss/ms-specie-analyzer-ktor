package io.specie.analyzer.adapters.outbound.persistence.exposed.repository

import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisCounterEntity
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisEntity
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisEntity.analyzedAt
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisEntity.dna
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisEntity.specie
import io.specie.analyzer.adapters.outbound.persistence.exposed.entities.SpecieAnalysisEntity.uuid
import io.specie.analyzer.application.domain.specie.SpecieAnalysis
import io.specie.analyzer.application.spi.Repository
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class SpecieAnalisysRepository : Repository<SpecieAnalysis, UUID> {

    override fun save(entity: SpecieAnalysis) {
        transaction {
            SpecieAnalysisEntity.insert {
                it[uuid] = entity.uuid.toString()
                it[dna] = entity.dna
                it[specie] = entity.identifier!!
                it[analyzedAt] = entity.analyzedAt!!
            }

            val updatedSpeciesCounter =
                SpecieAnalysisCounterEntity.update({ SpecieAnalysisCounterEntity.specie eq entity.identifier!! }) {
                    with(SqlExpressionBuilder) {
                        it.update(counter, counter + 1)
                    }
                }

            if (updatedSpeciesCounter != 1) {
                SpecieAnalysisCounterEntity.insert {
                    it[uuid] = UUID.nameUUIDFromBytes(entity.identifier!!.name.toByteArray()).toString()
                    it[specie] = entity.identifier!!
                }
            }
        }
    }

    override fun findById(id: UUID): SpecieAnalysis? {
        return transaction {
            SpecieAnalysisEntity
                .select { uuid eq id.toString() }
                .map {
                    SpecieAnalysis(
                        UUID.fromString(it[uuid]),
                        it[dna],
                        identifier = it[specie],
                        analyzedAt = it[analyzedAt]
                    )
                }.singleOrNull()
        }
    }
}
