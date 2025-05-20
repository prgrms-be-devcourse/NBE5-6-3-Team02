package com.grepp.smartwatcha.app.model.admin.user.service;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminSimpleRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponseDto;
import com.grepp.smartwatcha.app.model.admin.user.mapper.AdminUserMapper;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserJpaRepository;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserRatingJpaRepository;
import com.grepp.smartwatcha.app.model.admin.user.code.AdminUserSpecifications;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class AdminUserJpaService {

  private final AdminUserJpaRepository adminUserJpaRepository;
  private final AdminUserRatingJpaRepository adminUserRatingJpaRepository;
  private final UserJpaRepository userJpaRepository;

  public Page<AdminUserListResponseDto> findUserByFilter(String keyword, Role role, Boolean activated, Pageable pageable) {
    Specification<UserEntity> spec = Specification
        .where(AdminUserSpecifications.hasName(keyword))
        .and(AdminUserSpecifications.hasRole(role))
        .and(AdminUserSpecifications.isActivated(activated));

    return adminUserJpaRepository.findAll(spec, pageable)
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())));
  }

  public AdminUserListResponseDto findUserByName(String name) {
    return adminUserJpaRepository.findFirstByNameContainingIgnoreCase(name)
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())))
        .orElse(null);
  }

  public AdminUserListResponseDto findUserById(Long id) {
    return adminUserJpaRepository.findById(id)
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())))
        .orElse(null);
  }

  public void updateActivationStatus(Long id, boolean activated) {
    UserEntity user = userJpaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    user.setActivated(activated);
    userJpaRepository.save(user);

    log.info("🔧 [관리자] 사용자 활성 상태 변경: id={}, nowActivated={}", id, activated);
  }

  private List<AdminSimpleRatingDto> getRecentRatings(Long userId) {
    return adminUserRatingJpaRepository.findTop8ByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(rating -> AdminSimpleRatingDto.builder()
            .title(rating.getMovie().getTitle())
            .score(rating.getScore())
            .createdAt(rating.getCreatedAt())
            .build())
        .toList();
  }
}
