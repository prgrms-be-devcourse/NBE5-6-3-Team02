package com.grepp.smartwatcha.app.model.search.service;

import com.grepp.smartwatcha.app.model.search.dto.SearchResultDto;
import com.grepp.smartwatcha.app.model.search.repository.SearchNeo4jRepository;
import com.grepp.smartwatcha.infra.neo4j.node.GenreNode;
import com.grepp.smartwatcha.infra.neo4j.node.MovieNode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(transactionManager = "neo4jTransactionManager")
public class SearchNeo4jService {

    private final SearchNeo4jRepository searchNeo4jRepository;


    public List<Long> findByGenre(String genre) {
        List<MovieNode> movies = searchNeo4jRepository.findByGenre(genre);
        List<Long> ids = new ArrayList<>();
        for (MovieNode movie : movies) {
            ids.add(movie.getId());
        }

        return ids;
    }

    public List<Long> findByActor(String actor) {
        List<MovieNode> movies = searchNeo4jRepository.findByActor(actor);
        List<Long> ids = new ArrayList<>();
        for (MovieNode movie : movies) {
            ids.add(movie.getId());
        }

        return ids;
    }

    public List<Long> findByDirector(String director) {
        List<MovieNode> movies = searchNeo4jRepository.findByDirector(director);
        List<Long> ids = new ArrayList<>();
        for (MovieNode movie : movies) {
            ids.add(movie.getId());
        }

        return ids;
    }

    public List<Long> findByWriter(String writer) {
        List<MovieNode> movies = searchNeo4jRepository.findByWriter(writer);
        List<Long> ids = new ArrayList<>();
        for (MovieNode movie : movies) {
            ids.add(movie.getId());
        }

        return ids;
    }
}
