package com.grepp.smartwatcha.app.model.index.service;

import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.index.repository.IndexJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = "jpaTransactionManager")
public class IndexJpaService {

    private final IndexJpaRepository indexJpaRepository;

    public List<IndexMovieDto> findByIds(List<Long> ids) {
        List<IndexMovieDto> indexMovieDtos = new ArrayList<>();
        for (Long id : ids) {
            Optional<MovieEntity> movieEntity = indexJpaRepository.findById(id);
            if (movieEntity.isPresent()) {
                IndexMovieDto indexMovieDto = IndexMovieDto.fromEntity(movieEntity.get());
                indexMovieDtos.add(indexMovieDto);
            }
        }
        return indexMovieDtos;
    }

    public List<IndexMovieDto> findByReleaseDate() {
        List<MovieEntity> movies = indexJpaRepository.findByReleaseDate();
        List<IndexMovieDto> indexMovieDtos = new ArrayList<>();
        for (MovieEntity movie : movies) {
            IndexMovieDto indexMovieDto = IndexMovieDto.fromEntity(movie);
            indexMovieDtos.add(indexMovieDto);
        }
        return indexMovieDtos;
    }

    public List<IndexMovieDto> findByRandom() {
        List<MovieEntity> movies = indexJpaRepository.findByRandom();
        List<IndexMovieDto> indexMovieDtos = new ArrayList<>();
        for (MovieEntity movie : movies) {
            IndexMovieDto indexMovieDto = IndexMovieDto.fromEntity(movie);
            indexMovieDtos.add(indexMovieDto);
        }
        return indexMovieDtos;
    }

    public List<IndexMovieDto> findByInterest(Long id) {
        List<MovieEntity> movies = indexJpaRepository.findByInterest(id);
        List<IndexMovieDto> indexMovieDtos = new ArrayList<>();
        for (MovieEntity movie : movies) {
            IndexMovieDto indexMovieDto = IndexMovieDto.fromEntity(movie);
            indexMovieDtos.add(indexMovieDto);
        }
        return indexMovieDtos;
    }
}
