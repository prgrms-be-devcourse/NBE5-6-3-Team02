package com.grepp.smartwatcha.app.model.admin.tag;

import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class AdminTagJpaService {

  private final AdminTagJpaRepository adminTagJpaRepository;

  // 태그를 키워드로 검색(대소문자 무시)하거나 전체 태그를 페이징 조회
  public Page<TagEntity> findTagsByKeyword(String keyword, Pageable pageable) {
    if  (keyword == null || keyword.isBlank()){
      return adminTagJpaRepository.findAll(pageable);
    } else {
      return adminTagJpaRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
  }
}
