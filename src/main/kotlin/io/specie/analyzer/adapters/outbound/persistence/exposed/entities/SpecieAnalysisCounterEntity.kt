package io.specie.analyzer.adapters.outbound.persistence.exposed.entities

import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import org.jetbrains.exposed.sql.Table

object SpecieAnalysisCounterEntity : Table("SPECIE.SPECIES_ANALYSIS_COUNTER") {

    val uuid = varchar("UUID", 36)
    val specie = enumerationByName("SPECIE", 50, SpecieIdentifier::class)
    val counter = long("COUNTER").default(1)
}
