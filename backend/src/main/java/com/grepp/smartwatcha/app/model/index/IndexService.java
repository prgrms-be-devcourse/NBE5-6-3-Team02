package com.grepp.smartwatcha.app.model.index;

import com.grepp.smartwatcha.app.model.index.dto.IndexMovieDto;
import com.grepp.smartwatcha.app.model.index.repository.IndexNeo4jRepository;
import com.grepp.smartwatcha.app.model.index.service.IndexJpaService;
import com.grepp.smartwatcha.app.model.index.service.IndexNeo4jService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexService {

    private final IndexJpaService indexJpaService;
    private final IndexNeo4jService indexNeo4jService;

    public List<IndexMovieDto> findByReleaseDate() {
        return indexJpaService.findByReleaseDate();
    }

    public List<IndexMovieDto> findByRandom() {
        return indexJpaService.findByRandom();
    }

    public List<IndexMovieDto> findLightMovies() {
        List<Long> ids = indexNeo4jService.findLightMovies();
        log.info("lightMovies: {}", indexJpaService.findByIds(ids));

        return indexJpaService.findByIds(ids);
    }

    public List<IndexMovieDto> findByInterest(Long id) {
        return indexJpaService.findByInterest(id);
    }

    public List<IndexMovieDto> findByReleaseDateByAge(boolean isAdult) {
        return indexJpaService.findByReleaseDateByAge(isAdult);
    }

    public List<IndexMovieDto> findByRandomByAge(boolean isAdult) {
        return indexJpaService.findByRandomByAge(isAdult);
    }
}
