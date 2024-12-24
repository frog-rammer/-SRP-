package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberMemberIdAndReadFalse(String memberId); // 특정 사용자의 읽지 않은 알림 조회

}