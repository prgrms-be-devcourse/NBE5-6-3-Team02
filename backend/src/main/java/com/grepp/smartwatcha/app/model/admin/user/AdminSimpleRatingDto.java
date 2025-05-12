package com.grepp.smartwatcha.app.model.admin.user;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSimpleRatingDto {
  private String title;
  private Double score;
  private LocalDateTime createdAt;
}
