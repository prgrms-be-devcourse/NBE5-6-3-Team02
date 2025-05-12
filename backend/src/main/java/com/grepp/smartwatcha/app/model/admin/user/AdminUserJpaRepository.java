package com.grepp.smartwatcha.app.model.admin.user;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminUserJpaRepository  extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
  List<UserEntity> findAll();
  Optional<UserEntity> findFirstByNameContainingIgnoreCase(String name);
}
