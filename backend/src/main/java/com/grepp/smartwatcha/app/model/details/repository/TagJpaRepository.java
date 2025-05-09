package com.grepp.smartwatcha.app.model.details.repository;

import com.grepp.smartwatcha.infra.jpa.entity.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagJpaRepository extends JpaRepository<TagsEntity, Long> {
    List<TagsEntity> findById(int id);
}
