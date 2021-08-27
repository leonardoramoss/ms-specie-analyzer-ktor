package io.specie.analyzer.configuration

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.HoconApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun databaseConfiguration() {

    val configuration = HoconApplicationConfig(ConfigFactory.load()).config("ktor.datasource")

    val hikariConfig = HikariConfig().apply {
        poolName = configuration.property("name").getString()
        jdbcUrl = configuration.property("url").getString()
        username = configuration.property("username").getString()
        password = configuration.property("password").getString()
        driverClassName = configuration.property("className").getString()
        maximumPoolSize = 3
        isAutoCommit = false
    }

    Database.connect(HikariDataSource(hikariConfig))

    transaction {
        // SchemaUtils.createSchema(Schema("SPECIE"))
        SchemaUtils.setSchema(Schema("SPECIE"))
        // SchemaUtils.create(SpecieAnalysisEntity)
        // SchemaUtils.create(SpecieAnalysisCounterEntity)
    }
}
