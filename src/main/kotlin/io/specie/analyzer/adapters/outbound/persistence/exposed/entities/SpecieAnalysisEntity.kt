package io.specie.analyzer.adapters.outbound.persistence.exposed.entities

import io.specie.analyzer.application.domain.specie.SpecieIdentifier
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object SpecieAnalysisEntity : Table("SPECIE.SPECIES_ANALYSIS") {

    val uuid = varchar("UUID", 36)
    val dna = varchar("DNA", 200)
    val specie = enumerationByName("SPECIE", 50, SpecieIdentifier::class)
    val analyzedAt = datetime("ANALYZED_AT")

    override val primaryKey = PrimaryKey(uuid)
}
