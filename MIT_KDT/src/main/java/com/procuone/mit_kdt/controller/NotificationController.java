package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.entity.Notification;
import com.procuone.mit_kdt.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 알림 생성
    @PostMapping("/send")
    public String sendNotification(@RequestParam String memberId, @RequestParam String message ,@RequestParam String url) {
        notificationService.createNotification(memberId, message,url);
        return "Notification sent successfully";
    }

    // 읽지 않은 알림 조회
    @GetMapping("/unread/{memberId}")
    public List<Notification> getUnreadNotifications(@PathVariable String memberId) {
        return notificationService.getUnreadNotifications(memberId);
    }

    // 알림 읽음 처리
    @PostMapping("/markAsRead")
    public String markAsRead(@RequestParam Long notificationId) {
        notificationService.markAsRead(notificationId);
        return "Notification marked as read";
    }
}
