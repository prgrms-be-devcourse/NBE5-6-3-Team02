package com.grepp.smartwatcha.app.model.notification.repository;

import com.grepp.smartwatcha.infra.jpa.entity.NotificationEntity;
import com.grepp.smartwatcha.infra.jpa.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUserIdAndActivated(Long userId, Boolean activated);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.id = :id AND n.user.id = :userId")
    void markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    Long user(UserEntity user);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.activated = false WHERE n.id = :id AND n.user.id = :userId")
    void deactivateNotification(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsRead(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.activated = false WHERE n.user.id = :userId")
    void deactivateAllNotifications(@Param("userId") Long userId);

    Object countByUserIdAndIsReadFalse(Long id);
}
