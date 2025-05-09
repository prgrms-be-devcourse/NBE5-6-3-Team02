package com.grepp.smartwatcha.app.model.search.repository;

import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

@DataNeo4jTest
class SearchNeo4jRepositoryTest {

    @Autowired
    SearchNeo4jRepository searchNeo4jRepository;

    @Test
    void findByGenre() {
        String genre = "Action";
        List<MovieNode> movies = searchNeo4jRepository.findByGenre(genre);

        System.out.println(movies);
    }
}