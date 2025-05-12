package com.grepp.smartwatcha.app.model.admin.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRatingDto {
  private String title;
  private Double score;
  private LocalDateTime createdAt;
  private String userName;
}
