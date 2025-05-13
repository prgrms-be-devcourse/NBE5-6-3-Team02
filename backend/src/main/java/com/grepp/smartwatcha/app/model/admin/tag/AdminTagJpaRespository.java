package com.grepp.smartwatcha.app.model.admin.tag;

import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTagJpaRespository extends JpaRepository<TagEntity, Long> {
  List<TagEntity> findAll();
  Page<TagEntity> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
