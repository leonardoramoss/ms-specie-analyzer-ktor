package io.specie.analyzer.application.spi

import java.util.UUID

interface Repository<T, ID> {

    fun save(entity: T)

    fun findById(id: UUID): T?
}
