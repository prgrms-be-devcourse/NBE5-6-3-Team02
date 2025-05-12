package com.grepp.smartwatcha.app.model.admin.user;

import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class AdminUserJpaService {

  private final AdminUserJpaRepository adminUserJpaRepository;
  private final AdminUserRatingJpaRepository adminUserRatingJpaRepository;
  private final UserJpaRepository userJpaRepository;

  public Page<AdminUserListResponse> findUserByFilter(String keyword, String role, Boolean activated, Pageable pageable) {
    Specification<UserEntity> spec = Specification
        .where(AdminUserSpecifications.hasName(keyword))
        .and(AdminUserSpecifications.hasRole(role))
        .and(AdminUserSpecifications.isActivated(activated));

    return adminUserJpaRepository.findAll(spec, pageable)
        .map(user -> {
          List<AdminSimpleRatingDto> ratings = adminUserRatingJpaRepository
              .findTop8ByUserIdOrderByCreatedAtDesc(user.getId())
              .stream()
              .map(rating -> AdminSimpleRatingDto.builder()
                  .title(rating.getMovie().getTitle())
                  .score(rating.getScore())
                  .createdAt(rating.getCreatedAt())
                  .build())
              .toList();
          return AdminUserMapper.toDto(user, ratings);
        });
  }

  public AdminUserListResponse findUserByName(String name) {
    UserEntity user = adminUserJpaRepository.findFirstByNameContainingIgnoreCase(name)
        .orElse(null);
    if (user == null) return null;

    List<AdminSimpleRatingDto> ratings = adminUserRatingJpaRepository
        .findTop8ByUserIdOrderByCreatedAtDesc(user.getId())
        .stream()
        .map(rating -> AdminSimpleRatingDto.builder()
            .title(rating.getMovie().getTitle())
            .score(rating.getScore())
            .createdAt(rating.getCreatedAt())
            .build())
        .toList();

    return AdminUserMapper.toDto(user, ratings);
  }

  public AdminUserListResponse findUserById(Long id) {
    return adminUserJpaRepository.findById(id)
         .map(user -> {
          List<AdminSimpleRatingDto> ratings = adminUserRatingJpaRepository
              .findTop8ByUserIdOrderByCreatedAtDesc(user.getId())
              .stream()
              .map(rating -> AdminSimpleRatingDto.builder()
                  .title(rating.getMovie().getTitle())
                  .score(rating.getScore())
                  .createdAt(rating.getCreatedAt())
                  .build())
              .toList();
          return AdminUserMapper.toDto(user, ratings);
        })
        .orElse(null);
  }

  public void updateActivationStatus(Long id, boolean activated) {
    UserEntity user = userJpaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    if (user.getActivated() == null) {
      user.setActivated(false);
    }

    user.setActivated(activated);
    userJpaRepository.save(user);
  }

  public void deleteById(Long id) {
    userJpaRepository.deleteById(id);
  }
}
