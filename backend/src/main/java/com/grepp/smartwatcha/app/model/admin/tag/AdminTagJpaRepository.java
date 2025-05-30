package com.grepp.smartwatcha.app.model.admin.tag;

import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTagJpaRepository extends JpaRepository<TagEntity, Long> {
  // 태그 이름에 키워드가 포함된(대소문자 무시) 태그를 페이징 조회
  Page<TagEntity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
