package com.grepp.smartwatcha.app.model.details.service.jpaservice;

import com.grepp.smartwatcha.app.model.details.dto.jpadto.JpaTagDto;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieDetailsJpaRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.MovieTagRepository;
import com.grepp.smartwatcha.app.model.details.repository.jparepository.TagJapRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieTagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class TagJpaService {

    private final MovieTagRepository movieTagRepository;
    private final MovieDetailsJpaRepository movieDetailsJpaRepository;
    private final TagJapRepository tagRepository;

    public void selectTag(UserEntity user, Long movieId, String tagName) {
        MovieEntity movie = movieDetailsJpaRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화 없음"));

       List<TagEntity> tagList = tagRepository.findByName(tagName);
        if (tagList.isEmpty()) throw new RuntimeException("태그 없음");

        TagEntity tag = tagList.get(0);// 같으이름이면 첫번재꺼

        boolean exists = movieTagRepository.existsByUserAndMovieAndTag(user, movie, tag);
        if (exists) {
            throw new IllegalArgumentException("이미 남긴 태그입니다.");
        }
        MovieTagEntity entity = new MovieTagEntity(user, movie, tag);
        movieTagRepository.save(entity);
    }

    public List<MovieTagEntity> getUserTags(UserEntity user, Long movieId) {
        MovieEntity movie = movieDetailsJpaRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("영화 없음"));

        return movieTagRepository.findByUserAndMovie(user, movie);
    }


    public List<JpaTagDto> searchTags(String keyword) {
        return tagRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(tagEntity -> new JpaTagDto(tagEntity.getId(), tagEntity.getName()))
                .collect(Collectors.toList());
    }
}
