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
// MovieStatusChangedEvent 발생 시 작동하는 Listener
public class MovieStatusChangedEventListener {

    private final NotificationJpaRepository notificationJpaRepository;
    private final InterestUserJpaRepository interestUserJpaRepository;

    @EventListener
    /* 실제 Listener 작동 함수
     * 입력: Event
     * 출력: void
     * 이벤트 발생 대상 영화 id로 interest 테이블에 접근, 해당하는 영화에 보고싶어요 한 유저들에게 알림 발송
     */
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
