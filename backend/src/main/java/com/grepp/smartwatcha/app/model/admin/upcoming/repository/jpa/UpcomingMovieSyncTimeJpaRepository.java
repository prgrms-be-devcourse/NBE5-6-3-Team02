package com.grepp.smartwatcha.app.model.admin.upcoming.repository.jpa;

import com.grepp.smartwatcha.infra.jpa.entity.SyncTimeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpcomingMovieSyncTimeJpaRepository extends JpaRepository<SyncTimeEntity, String> {
  Optional<SyncTimeEntity> findByType(String time);
}
