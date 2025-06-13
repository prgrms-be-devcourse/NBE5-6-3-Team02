package com.grepp.smartwatcha.infra.jpa.entity;

import com.grepp.smartwatcha.infra.jpa.BaseEntity;
import com.grepp.smartwatcha.infra.jpa.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) // Role 이 반드시 부여되도록 수정
    private Role role;

    @Column(nullable = false)
    private LocalDate birth = LocalDate.now(); // 생년월일

    @Column(nullable = false)
    private Integer age; // 나이

    @Column(nullable = false)
    private Boolean isAdult; // 성인 여부 (만 19세 이상)
}
