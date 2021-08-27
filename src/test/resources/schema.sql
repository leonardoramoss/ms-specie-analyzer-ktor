CREATE SCHEMA IF NOT EXISTS SPECIE;

CREATE TABLE IF NOT EXISTS SPECIE.SPECIES_ANALYSIS (
    UUID        VARCHAR(36) NOT NULL,
    DNA         VARCHAR(200) NOT NULL,
    SPECIE      VARCHAR(50) NOT NULL,
    ANALYZED_AT TIMESTAMP NOT NULL,
    CONSTRAINT PK_SPECIES_ANALYSIS PRIMARY KEY (UUID)
);

CREATE TABLE IF NOT EXISTS SPECIE.SPECIES_ANALYSIS_COUNTER (
    UUID        VARCHAR(36) NOT NULL,
    SPECIE      VARCHAR(50) NOT NULL,
    COUNTER     NUMERIC NOT NULL,
    CONSTRAINT PK_SPECIES_ANALYSIS_COUNTER PRIMARY KEY (UUID)
);
