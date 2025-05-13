package com.grepp.smartwatcha.app.model.admin.user.code;

import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class AdminUserSpecifications {
  public static Specification<UserEntity> hasName(String name) {
    return (root, query, cb) ->
        (name == null || name.isBlank()) ? null :
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
  }

  public static Specification<UserEntity> hasRole(String role){
    return (root, query, cb) ->
        (role == null || role.isBlank()) ? null :
            cb.equal(root.get("role"), role);
  }

  public static Specification<UserEntity> isActivated(Boolean activated) {
    return (root, query, cb) ->
        (activated == null) ? null : cb.equal(root.get("activated"), activated);
  }
}
