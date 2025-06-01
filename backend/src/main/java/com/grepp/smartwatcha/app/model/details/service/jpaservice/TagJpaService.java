package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.TagDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.UserTagJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.TagJpaRepository;
import com.grepp.smartwatcha.app.model.details.service.neo4jservice.TagNeo4jService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class TagJpaService {

    private final UserTagJpaRepository userTagJpaRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;
    private final TagJpaRepository tagRepository;
    private final TagNeo4jService tagNeo4jService;


    // 유저가 선택한 태그 기반 저장
    // 저장 시 만약 해당 Tag exists 할경우 에러 반환
    public void selectTag(UserEntity user, Long movieId, String tagName) {
        MovieEntity movie = movieDetailsJpaRepository.findById(movieId)
                .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

       List<TagEntity> tagList = tagRepository.findByName(tagName);
       if (tagList.isEmpty()) throw new CommonException(ResponseCode.BAD_REQUEST);

       TagEntity tag = tagList.get(0);// 같은 이름이면 첫번재꺼

        boolean exists = userTagJpaRepository.existsByUserAndMovieAndTag(user, movie, tag);
        if (exists) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        MovieTagEntity entity = new MovieTagEntity(user, movie, tag);
        userTagJpaRepository.save(entity);

        // Neo4j Tagged 관계 저장 해줘야함
        tagNeo4jService.saveTaggedRelation(user, movieId, tagName);
    }

    // 유저가 남긴 Tag 정보 반환
    public List<MovieTagEntity> getUserTags(UserEntity user, Long movieId) {
        MovieEntity movie = movieDetailsJpaRepository.findById(movieId)
                .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

        return userTagJpaRepository.findByUserAndMovie(user, movie);
    }


    // TagEntity에 저장된 Tag 정보 List로 반환
    public List<TagDto> searchTags(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(tagEntity -> new TagDto(tagEntity.getId(), tagEntity.getName()))
                .collect(Collectors.toList());
    }

    // 유저가 남긴 태그 삭제
    public void deleteUserTag(UserEntity user, Long movieId, String tagName) {
        MovieTagEntity entity = userTagJpaRepository
                .findByUserAndMovieIdAndTag_Name(user,movieId,tagName)
                .orElseThrow(() -> new CommonException(ResponseCode.BAD_REQUEST));

        userTagJpaRepository.delete(entity);
        tagNeo4jService.deletTaggedRelation(user, movieId, tagName);

    }
}
