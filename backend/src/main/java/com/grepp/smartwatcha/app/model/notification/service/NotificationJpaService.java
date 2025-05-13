package com.grepp.smartwatcha.app.model.notification.service;

import com.grepp.smartwatcha.app.model.notification.dto.NotificationDto;
import com.grepp.smartwatcha.app.model.notification.event.MovieStatusChangedEvent;
import com.grepp.smartwatcha.app.model.notification.repository.MovieStatusChangeJpaRepository;
import com.grepp.smartwatcha.app.model.notification.repository.NotificationJpaRepository;
import com.grepp.smartwatcha.infra.jpa.entity.MovieEntity;
import com.grepp.smartwatcha.infra.jpa.entity.NotificationEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "jpaTransactionManager")
public class NotificationJpaService {

    private final MovieStatusChangeJpaRepository movieStatusChangeJpaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationJpaRepository notificationJpaRepository;

    @Scheduled(cron = "0 0 6 * * *")
    public void updateReleasedMovies() {
        Boolean status = Boolean.TRUE;

        List<MovieEntity> movies = movieStatusChangeJpaRepository.findByReleaseDateToday();

        for (MovieEntity movie : movies) {
            movie.setIsReleased(status);
            eventPublisher.publishEvent(new MovieStatusChangedEvent(movie, status));
            movieStatusChangeJpaRepository.save(movie);
        }

    }

    public List<NotificationDto> getActiveNotificationsForUser(Long userId) {
        List<NotificationEntity> notificationEntities = notificationJpaRepository.findByUserIdAndActivated(userId, Boolean.TRUE);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (NotificationEntity notificationEntity : notificationEntities) {
            NotificationDto notificationDto = new NotificationDto();
            notificationDto.setId(notificationEntity.getId());
            notificationDto.setMessage(notificationEntity.getMessage());
            notificationDto.setIsRead(notificationEntity.getIsRead());
            notificationDtos.add(notificationDto);
        }

        return notificationDtos;

    }

    public void markAsRead(Long id, Long userId) {
        notificationJpaRepository.markAsRead(id, userId);
    }

    public void deactivateNotification(Long notificationId, Long userId) {
        notificationJpaRepository.deactivateNotification(notificationId, userId);
    }

    public void markAllAsRead(Long userId) {
        notificationJpaRepository.markAllAsRead(userId);
    }

    public void deactivateAllNotifications(Long userId) {
        notificationJpaRepository.deactivateAllNotifications(userId);
    }
}
