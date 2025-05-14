package com.grepp.smartwatcha.app.model.details.neo4jservice;

import com.grepp.smartwatcha.app.model.details.neo4jdto.Neo4jTagDto;
import com.grepp.smartwatcha.app.model.details.neo4jrepository.TagNeo4jRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "neo4jTransactionManager")
public class TagNeo4jService {
    private final TagNeo4jRepository tagNeo4jRepository;

    public TagNeo4jService(TagNeo4jRepository tagNeo4jRepository) {
        this.tagNeo4jRepository = tagNeo4jRepository;
    }


    public void saveTagSelection(Long userId, Long movieId, String tagName) {
        tagNeo4jRepository.createTaggedRelation(userId, movieId, tagName);
    }

    public List<Neo4jTagDto> getTop6Tags(Long movieId) {
        return tagNeo4jRepository.findTop6TagsByMovieId(movieId);
    }
}
