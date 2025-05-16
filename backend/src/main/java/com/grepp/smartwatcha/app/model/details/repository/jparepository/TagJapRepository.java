package com.grepp.smartwatcha.app.model.details.repository.jparepository;

import com.grepp.smartwatcha.infra.jpa.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagJapRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findByName(String name);

    List<TagEntity> findByNameContainingIgnoreCase(String name);

}
