package com.procuone.mit_kdt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;

import java.time.LocalDate;

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

    // 부서번호에 따른 타입 설정
    public void setUserType() {
        switch (this.Dno) {
            case "00":
                this.type = "구매부서";
                break;
            case "01":
                this.type = "생산부서";
                break;
            case "02":
                this.type = "자재부서";
                break;
            case "03":
                this.type = "협력업체";
                break;
            default:
                this.type = "기타";
        }
    }

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    // 엔티티가 DB에 저장되기 전에 호출되는 메서드
    @PrePersist
    public void prePersist() {
        if (creationDate == null) {
            this.creationDate = LocalDate.now();  // 현재 날짜를 자동으로 할당
        }
    }
}
