package com.grepp.smartwatcha.infra.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.neo4j.core.Neo4jClient;

@Component
@RequiredArgsConstructor
public class IndexInitializer {

    private final Neo4jClient neo4jClient;

    @PostConstruct
    @Transactional("neo4jTransactionManager")
    public void createIndexes() {
        neo4jClient.query("""
            CREATE INDEX movie_id_index IF NOT EXISTS
            FOR (m:MOVIE)
            ON (m.id)
        """).run();

        neo4jClient.query("""
            CREATE INDEX genre_name_index IF NOT EXISTS
            FOR (g:GENRE)
            ON (g.name)
        """).run();

        neo4jClient.query("""
            CREATE INDEX tag_name_index IF NOT EXISTS
            FOR (t:TAG)
            ON (t.name)
        """).run();
    }
}