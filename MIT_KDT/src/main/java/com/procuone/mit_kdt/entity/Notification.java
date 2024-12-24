package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 다대일 관계 설정
    @JoinColumn(name = "member_id", nullable = false) // 외래 키 이름
    private Member member; // 연관 관계의 주인

    private String message; // 알림 메시지

    private String type;
    private String url;

    @Column(nullable = false)
    private boolean read = false; // 읽음 여부

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간
}
