package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_test")  // 테이블 이름을 'member_test'로 설정
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 자동 증가 설정
    private String memberId;   // 회원 ID를 String 타입으로 변경
    private String memberName; // 회원 이름
    private String password;   // 비밀번호
    private String email;      // 이메일
    private String phone;      // 전화번호
    private String type;       // 부서 타입 (구매부서, 생산부서 등)
    private String dno;        // 부서 번호
    private LocalDateTime creationDate;  // 생성일

    public Member(String memberName, String password, String email, String phone, String type, String dno) {
        this.memberName = memberName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.dno = dno;
        this.creationDate = LocalDateTime.now();  // 생성일은 현재 날짜로 설정
    }

    // 기본 생성자
    public Member() {
    }

    public Member(String s, String s1, String s2, String s3, String s4, String 협력업체, String s5) {
    }

    // Getter 및 Setter 메소드들
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
