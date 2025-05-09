package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TagsEntity {
    @Id
    private int id;

    private String name;

}
