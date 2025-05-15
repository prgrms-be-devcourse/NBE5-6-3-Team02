package com.grepp.smartwatcha.app.model.notification.event;

import com.grepp.smartwatcha.app.model.notification.repository.InterestUserJpaRepository;
import com.grepp.smartwatcha.app.model.notification.repository.NotificationJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.InterestEntity;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.NotificationEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MovieStatusChangedEventListener {

    private final NotificationJpaRepository notificationJpaRepository;
    private final InterestUserJpaRepository interestUserJpaRepository;

    @EventListener
    public void handleMovieStatusChanged(MovieStatusChangedEvent event) {
        MovieEntity movie = event.getMovie();
        Long movieId = movie.getId();

        List<InterestEntity> interests = interestUserJpaRepository.findByMovieId(movieId);
        for (InterestEntity interest : interests) {
            NotificationEntity notification = new NotificationEntity();
            notification.setUser(interest.getUser());
            notification.setMessage("🎬 오늘부터 [" + movie.getTitle() + "] 시청 가능!");
            notificationJpaRepository.save(notification);
        }
    }

}
