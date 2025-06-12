package com.grepp.smartwatcha.app.model.notification.event;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
// 영화 개봉 정보 변경을 위한 Event 객체
public class MovieStatusChangedEvent {

    private final MovieEntity movie;
    private final Boolean isReleased;
}
