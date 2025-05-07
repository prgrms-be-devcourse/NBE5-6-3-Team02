package com.grepp.smartwatcha.infra.neo4j.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @Transactional(transactionManager = "neo4jTransactionManager")
    public void findAll() {
        Optional<MovieNode> list = movieRepository.findByTitle("Inception");
        System.out.println(list);
    }
}