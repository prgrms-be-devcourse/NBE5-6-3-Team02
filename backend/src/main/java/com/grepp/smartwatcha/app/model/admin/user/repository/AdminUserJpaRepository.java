package com.grepp.smartwatcha.app.model.admin.user.repository;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Admin User List 페이지에서 사용하는 유저 관련 JPA 레포지토리
@Repository
public interface AdminUserJpaRepository  extends JpaRepository<UserEntity, Long> {

  // 유저 필터 검색
  // 검색 조건이 null 인 경우 해당 조건은 무시됨 (동적 조건)
  @Query("SELECT u FROM UserEntity u WHERE " +
      "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
      "(:role IS NULL OR u.role = :role) AND " +
      "(:activated IS NULL OR u.activated = :activated)")
  Page<UserEntity> findUserByFilter(
      @Param("name") String name,
      @Param("role") Role role,
      @Param("activated") Boolean activated,
      Pageable pageable
  );

  // Admin UserList 모달 - keyword(user_name)를 포함하는 모든 유저를 대소문자 구분 없이 조회
  List<UserEntity> findAllByNameContainingIgnoreCase(String name);

  // 대시보드용 통계 메서드
  long countByActivatedTrue();   // 활성화된 유저 수 반환 (activated = true)
  long countByActivatedFalse();  // 비활성화된 유저 수 반환 (activated = false)
}
