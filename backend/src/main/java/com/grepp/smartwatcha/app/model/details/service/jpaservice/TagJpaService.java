package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.JpaTagDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.TagJapRepository;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class TagJpaService {

    private final TagJapRepository tagRepository;
    private final TagNeo4jService tagNeo4jService;

    public List<JpaTagDto> searchTags(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(tagEntity -> new JpaTagDto(tagEntity.getId(), tagEntity.getName()))
                .collect(Collectors.toList());
    }
}
