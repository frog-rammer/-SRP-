package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "members")  // 테이블 이름을 'members'로 설정
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 증가하는 ID

    private String memberId;  // 회원 ID
    private String memberName; // 회원 이름
    private String password;   // 비밀번호
    private String email;      // 이메일
    private String phone;      // 전화번호
    private String type;       // 부서 타입 (구매부서, 자재부서, 생산부서 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")  // 외래 키 (Company와의 관계 설정)
    private Company company; // 소속된 회사

    // 기본 생성자
    public Member() {}

    // 생성자
    public Member(String memberId, String memberName, String password, String email, String phone, String type, Company company) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.company = company;
    }

    // Getter 및 Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", memberId='" + memberId + '\'' +
                ", memberName='" + memberName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", type='" + type + '\'' +
                ", company=" + company +
                '}';
    }

    public void setDno(String dno) {
    }
}
