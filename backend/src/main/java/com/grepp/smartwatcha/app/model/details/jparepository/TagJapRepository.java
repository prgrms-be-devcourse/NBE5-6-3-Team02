package com.grepp.smartwatcha.app.model.details.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJapRepository extends JpaRepository<TagEntity, Long> {
}
