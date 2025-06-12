package com.grepp.smartwatcha.app.model.index.service;

import com.grepp.smartwatcha.app.model.index.repository.IndexNeo4jRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "neo4jTransactionManager")
public class IndexNeo4jService {

    private final IndexNeo4jRepository indexNeo4jRepository;

    public List<Long> findLightMovies() {
        return indexNeo4jRepository.findLightMovies();
    }
}
