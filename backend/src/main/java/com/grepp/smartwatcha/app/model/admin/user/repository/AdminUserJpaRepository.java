package com.grepp.smartwatcha.app.model.admin.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminUserJpaRepository  extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
  List<UserEntity> findAll();
  Optional<UserEntity> findFirstByNameContainingIgnoreCase(String name);

  // 대시보드용 통계 메서드
  long countByActivatedTrue();   // 활성 유저 수
  long countByActivatedFalse();  // 비활성 유저 수
}
