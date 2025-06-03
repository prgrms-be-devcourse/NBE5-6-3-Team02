package com.grepp.smartwatcha.app.model.details.service;


import com.grepp.smartwatcha.app.model.details.dto.jpadto.TagDto;
import com.grepp.smartwatcha.app.model.details.dto.neo4jdto.TagCountRequestDto;
import com.grepp.smartwatcha.app.model.details.service.jpaservice.TagJpaService;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagJpaService tagJpaService;
    private final TagNeo4jService tagNeo4jService;


    // 태그 저장 및 Neo4j 횟수 증가
    public void saveUserTag(UserEntity user, Long movieId, String tagName) {
        // JPA 저장
        tagJpaService.selectTag(user, movieId, tagName);

        // Neo4j 선택 횟수 증가
        tagNeo4jService.saveTagSelection(user,movieId, tagName);
    }

    // 유저가 남긴 태그 조회
    public List<MovieTagEntity> getUserTags(UserEntity user, Long movieId) {
        return tagJpaService.getUserTags(user,movieId);
    }

    // 추천용 상위 태그 조회
    public List<TagCountRequestDto> getTop6Tags(Long movieId) {
        return tagNeo4jService.getTop6Tags(movieId);
    }

    public List<TagDto> searchTags(String keyword) {
        return tagJpaService.searchTags(keyword);
    }
    public void deleteUserTag(UserEntity user, Long movieId, String tagName) {
        tagJpaService.deleteUserTag(user, movieId, tagName);
    }
}
