package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.entity.Notification;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.repository.NotificationRepository;
import com.procuone.mit_kdt.service.NotificationService;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, MemberRepository memberRepository) {
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void createNotification(String memberId, String message,String url) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Notification notification = Notification.builder()
                .member(member)
                .message(message)
                .url(url)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getUnreadNotifications(String memberId) {
        return notificationRepository.findByMemberMemberIdAndReadFalse(memberId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
