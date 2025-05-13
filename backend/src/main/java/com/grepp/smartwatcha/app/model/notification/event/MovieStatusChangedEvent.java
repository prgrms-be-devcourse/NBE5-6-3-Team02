package com.grepp.smartwatcha.app.model.notification.event;

import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class MovieStatusChangedEvent {

    private final MovieEntity movie;
    private final Boolean isReleased;
}
