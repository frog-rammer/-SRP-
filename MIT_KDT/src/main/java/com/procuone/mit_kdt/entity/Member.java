package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member{
    @Id
    private String memberId;
    private String memberName;
    private String password;
    private String Dno;
    private String email;
    private String phone;
    private String type;  // 사용자 타입 추가


    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    // 엔티티가 DB에 저장되기 전에 호출되는 메서드
    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            this.creationDate = LocalDate.now();  // 현재 날짜를 자동으로 할당
        }
    }
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Notification> notifications = new ArrayList<>(); // 알림 리스트
}
