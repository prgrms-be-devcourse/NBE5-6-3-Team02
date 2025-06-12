package com.grepp.smartwatcha.app.controller.api.notification;

import com.grepp.smartwatcha.app.model.notification.NotificationJpaService;
import com.grepp.smartwatcha.infra.error.exceptions.CommonException;
import com.grepp.smartwatcha.infra.response.ApiResponse;
import com.grepp.smartwatcha.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
// 알림 읽음, 삭제용 api 컨트롤러
public class NotificationApiController {

    private final NotificationJpaService notificationService;

    @PostMapping("read")
    @ResponseBody
    /*
     * 단일 알림 읽음 처리
     * 입력: userId, notificationId
     * 출력: 200 OK, 예외 발생 시 400 BAD REQUEST
     * 로직: userId, notificationId로 접근, 해당하는 알림의 isRead를 true로 전환
     */
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestParam("id") Long notificationId,
            @RequestParam("user") Long userId) {
        try {
            notificationService.markAsRead(notificationId, userId);

            return ResponseEntity.ok(ApiResponse.noContent());
        } catch (Exception e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }

    @PostMapping("delete")
    @ResponseBody
    /*
     * 단일 알림 삭제 처리
     * 입력: userId, notificationId
     * 출력: 200 OK, 예외 발생 시 400 BAD REQUEST
     * 로직: userId, notificationId로 접근, 해당하는 알림의 activated를 false로 전환
     */
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @RequestParam("id") Long notificationId,
            @RequestParam("user") Long userId) {
        try {
            notificationService.deactivateNotification(notificationId, userId);

            return ResponseEntity.ok(ApiResponse.noContent());
        } catch (Exception e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }

    @PostMapping("readAll")
    @ResponseBody
    /*
     * 단일 알림 삭제 처리
     * 입력: userId
     * 출력: 200 OK, 예외 발생 시 400 BAD REQUEST
     * 로직: userId로 접근, 해당하는 모든 알림의 isRead를 true로 전환
     */
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam("user") Long userId) {
        try{
            notificationService.markAllAsRead(userId);

            return ResponseEntity.ok(ApiResponse.noContent());
        } catch (Exception e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }

    @PostMapping("deleteAll")
    @ResponseBody
    /*
     * 단일 알림 삭제 처리
     * 입력: userId
     * 출력: 200 OK, 예외 발생 시 400 BAD REQUEST
     * 로직: userId로 접근, 해당하는 모든 알림의 activated를 false로 전환
     */
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications(@RequestParam("user") Long userId) {
        try{
            notificationService.deactivateAllNotifications(userId);

            return ResponseEntity.ok(ApiResponse.noContent());
        } catch (Exception e) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }
}