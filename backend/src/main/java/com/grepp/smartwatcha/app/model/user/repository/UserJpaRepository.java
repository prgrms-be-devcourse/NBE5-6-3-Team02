package com.grepp.smartwatcha.app.model.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmailAndActivatedIsTrue(String email);
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByNameAndPhoneNumber(String name, String phoneNumber);
} 