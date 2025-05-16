package com.grepp.smartwatcha.app.model.admin.upcoming.service.jpa;

import com.grepp.smartwatcha.app.model.admin.upcoming.repository.jpa.UpcomingMovieSyncTimeJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpcomingMovieSyncTimeJpaService {
  private final UpcomingMovieSyncTimeJpaRepository upcomingMovieSyncTimeJpaRepository;

  public void update(String type){
    upcomingMovieSyncTimeJpaRepository.save(
        SyncTimeEntity.builder()
            .type(type)
            .syncTime(LocalDateTime.now())
            .build()
    );
  }

  public LocalDateTime getLastSyncTime(String type){
    return upcomingMovieSyncTimeJpaRepository.findById(type)
        .map(SyncTimeEntity::getSyncTime)
        .orElse(null);
  }
}
