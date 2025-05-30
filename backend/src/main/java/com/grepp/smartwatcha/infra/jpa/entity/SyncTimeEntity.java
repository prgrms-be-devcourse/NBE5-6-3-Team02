package com.grepp.smartwatcha.infra.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "syncTime")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// 공개 예정작 동기화 작업의 시간 및 결과 통계 정보를 저장하는 엔티티
public class SyncTimeEntity {
  @Id
  private String type; // 동기화 종류('upcoming')

  private LocalDateTime syncTime; // 동기화 시각

  private int newlyAddedCount; // 새로 추가된 항목 수

  private int failedCount; // 동기화 실패 수

  @Column(nullable = false)
  private int enrichFailedCount; // enrich 단계 실패 수
}
