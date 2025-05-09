package com.grepp.smartwatcha.app.details.repository;

import com.grepp.smartwatcha.infra.jpa.entity.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<TagsEntity, Long> {
    List<TagsEntity> findById(int id);
}
