package com.grepp.smartwatcha.infra.neo4j.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

@DataNeo4jTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void findAll() {
        Optional<MovieNode> list = movieRepository.findByTitle("Inception");
        System.out.println(list);
    }
}