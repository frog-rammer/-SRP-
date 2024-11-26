package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company_test")
public class Company {

    @Id
    private String companyId;  // String 타입으로 회사 ID

    private String businessId;  // 사업자 등록번호
    private String comName;     // 회사명
    private String comId;       // 회사 고유 ID
    private String comAdd;      // 회사 주소
    private String comEmail;    // 회사 이메일
    private String comPhone;    // 회사 전화번호
    private String comPaymentInfo;  // 결제 정보
    private String comBank;     // 은행명
    private String comAccountNumber;  // 계좌 번호
    private String comManage;  // 담당자 이름

    @Transient
    private String companyType;  // 협력업체 타입 추가 (DB에는 저장되지 않음)


    public Company(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String 우리은행, String s8, String s9) {
    }

    public Company() {

    }

    @PrePersist
    public void prePersist() {
        if (companyId == null) {
            companyId = generateCompanyId();  // companyId가 null이면 자동으로 값 설정
        }
    }

    private String generateCompanyId() {
        return "COMP-" + System.currentTimeMillis();  // 예시: 회사 ID를 현재 시간으로 생성
    }

    // Getter 및 Setter 메소드들
    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getComAdd() {
        return comAdd;
    }

    public void setComAdd(String comAdd) {
        this.comAdd = comAdd;
    }

    public String getComEmail() {
        return comEmail;
    }

    public void setComEmail(String comEmail) {
        this.comEmail = comEmail;
    }

    public String getComPhone() {
        return comPhone;
    }

    public void setComPhone(String comPhone) {
        this.comPhone = comPhone;
    }

    public String getComPaymentInfo() {
        return comPaymentInfo;
    }

    public void setComPaymentInfo(String comPaymentInfo) {
        this.comPaymentInfo = comPaymentInfo;
    }

    public String getComBank() {
        return comBank;
    }

    public void setComBank(String comBank) {
        this.comBank = comBank;
    }

    public String getComAccountNumber() {
        return comAccountNumber;
    }

    public void setComAccountNumber(String comAccountNumber) {
        this.comAccountNumber = comAccountNumber;
    }

    public String getComManage() {
        return comManage;
    }

    public void setComManage(String comManage) {
        this.comManage = comManage;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
}
