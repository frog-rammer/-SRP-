package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "companies_test")  // 테이블 이름을 'companies_test'로 변경
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 자동 증가하는 ID

    private String companyName;  // 회사명
    private String businessId;   // 사업자 번호
    private String comId;        // 회사 ID
    private String comAdd;       // 회사 주소
    private String comEmail;     // 회사 이메일
    private String comPhone;     // 회사 연락처
    private String comPaymentInfo;  // 결제 정보
    private String comBank;      // 은행명
    private String comAccountNumber;  // 계좌번호
    private String companyType = "협력업체";  // 회사 타입 (협력업체로 고정)

    // 추가된 필드
    private String comManage;  // 회사 담당자
    private String comName;    // 회사 이름 (테스트 협력업체 등)

    // 기본 생성자
    public Company() {}

    // 회사명, 사업자번호, 회사아이디를 받는 생성자
    public Company(String companyName, String businessId, String comId) {
        this.companyName = companyName;
        this.businessId = businessId;
        this.comId = comId;
    }

    // 모든 필드를 받는 생성자
    public Company(String companyName, String businessId, String comId,
                   String comAdd, String comEmail, String comPhone, String comPaymentInfo,
                   String comBank, String comAccountNumber, String comManage, String comName) {
        this.companyName = companyName;
        this.businessId = businessId;
        this.comId = comId;
        this.comAdd = comAdd;
        this.comEmail = comEmail;
        this.comPhone = comPhone;
        this.comPaymentInfo = comPaymentInfo;
        this.comBank = comBank;
        this.comAccountNumber = comAccountNumber;
        this.comManage = comManage;
        this.comName = comName;
        this.companyType = "협력업체"; // 회사 타입을 '협력업체'로 고정
    }

    // 이 생성자는 현재 불필요할 수 있지만, 그대로 유지되어야 하는 요구 사항이 있으면 삭제하지 않음
    public Company(String s, String companyType, String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8) {
        // 추가적인 로직이 없는 생성자는 빈 구현을 유지
    }

    // Getter 및 Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
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

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    // 추가된 메서드
    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getComManage() {
        return comManage;
    }

    public void setComManage(String comManage) {
        this.comManage = comManage;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", businessId='" + businessId + '\'' +
                ", comId='" + comId + '\'' +
                ", comAdd='" + comAdd + '\'' +
                ", comEmail='" + comEmail + '\'' +
                ", comPhone='" + comPhone + '\'' +
                ", comPaymentInfo='" + comPaymentInfo + '\'' +
                ", comBank='" + comBank + '\'' +
                ", comAccountNumber='" + comAccountNumber + '\'' +
                ", companyType='" + companyType + '\'' +
                ", comManage='" + comManage + '\'' +
                ", comName='" + comName + '\'' +
                '}';
    }
}
