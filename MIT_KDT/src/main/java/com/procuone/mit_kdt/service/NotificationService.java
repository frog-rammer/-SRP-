package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.Notification;

import java.util.List;

public interface NotificationService {
    void createNotification(String memberId, String message,String url); // 알림 생성
    List<Notification> getUnreadNotifications(String memberId); // 읽지 않은 알림 조회
    void markAsRead(Long notificationId); // 알림 읽음 처리
}
