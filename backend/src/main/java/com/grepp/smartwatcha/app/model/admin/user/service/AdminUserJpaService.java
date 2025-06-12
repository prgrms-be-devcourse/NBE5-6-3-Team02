package com.grepp.smartwatcha.app.model.admin.user.service;

import com.grepp.smartwatcha.app.model.admin.user.dto.AdminSimpleRatingDto;
import com.grepp.smartwatcha.app.model.admin.user.dto.AdminUserListResponse;
import com.grepp.smartwatcha.app.model.admin.user.mapper.AdminUserMapper;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserJpaRepository;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserRatingJpaRepository;
import com.grepp.smartwatcha.app.model.admin.user.repository.AdminUserTagJpaRepository;
import com.grepp.smartwatcha.app.model.user.repository.UserJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
// Admin User List 페이지에서 유저 관리 및 유저 평가 데이터를 처리하는 서비스 클래스
public class AdminUserJpaService {

  private final AdminUserJpaRepository adminUserJpaRepository;
  private final AdminUserRatingJpaRepository adminUserRatingJpaRepository;
  private final AdminUserTagJpaRepository adminUserTagJpaRepository;
  private final UserJpaRepository userJpaRepository;

  // 유저 필터 조회(이름, 역할, 활성화 여부)
  public Page<AdminUserListResponse> findUserByFilter(String keyword, Role role, Boolean activated,
      Pageable pageable) {
    return adminUserJpaRepository.findUserByFilter(keyword, role, activated, pageable)
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())));
  }

  // 입력된 이름(keyword)이 포함된 유저 전체를 대소문자 구분 없이 조회하고,
  // 각 유저의 최근 평점을 포함한 DTO 리스트 반환
  public List<AdminUserListResponse> findUserByName(String name) {
    return adminUserJpaRepository.findAllByNameContainingIgnoreCase(name).stream()
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())))
        .collect(Collectors.toList());
  }

  // 유저 ID로 유저 정보를 조회하고, 최근 평가 정보를 포함한 DTO 로 반환하여 반환
  public AdminUserListResponse findUserById(Long id) {
    return adminUserJpaRepository.findById(id)
        .map(user -> AdminUserMapper.toDto(user, getRecentRatings(user.getId())))
        .orElse(null);
  }

  // 유저의 활성화 상태를 변경(활성 <-> 비활성)
  public void updateActivationStatus(Long id, boolean activated) {
    UserEntity user = userJpaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    user.setActivated(activated);
    userJpaRepository.save(user);

    log.info("🔧 [관리자] 사용자 활성 상태 변경: id={}, nowActivated={}", id, activated);
  }

  // 유저의 최근 평가 8건을 간단한 DTO 로 조회
  private List<AdminSimpleRatingDto> getRecentRatings(Long userId) {
    return adminUserRatingJpaRepository.findTop8ByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(rating -> {
          Long movieId = rating.getMovie().getId();

          // user + movie 기준으로 태그 가져오기
          List<String> tagNames = adminUserTagJpaRepository
              .findByUserIdAndMovieId(userId, movieId)
              .stream()
              .map(mt -> mt.getTag().getName())
              .collect(Collectors.toList());

          return AdminSimpleRatingDto.builder()
              .movieId(movieId)
              .title(rating.getMovie().getTitle())
              .score(rating.getScore())
              .createdAt(rating.getCreatedAt())
              .tags(tagNames)
              .build();
        })
        .toList();
  }
}
