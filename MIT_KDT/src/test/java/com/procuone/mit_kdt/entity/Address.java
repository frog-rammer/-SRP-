package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address_test")  // 테이블 이름을 address_test로 지정
public class Address {
    // 클래스 정의...



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postalCode; // 우편번호
    private String province; // 도/시
    private String cityDistrict; // 시/구
    private String town; // 동/읍/면
    private String roadCode; // 도로코드
    private String roadName; // 도로명
    private String belowGround; // 지하층 여부
    private String mainBuildingNumber; // 건물번호
    private String subBuildingNumber; // 세부 건물번호
    private String buildingManagementNumber; // 관리번호
    private String bulkDeliveryName; // 집합배송지명
    private String cityDistrictBuildingName; // 시/구 건물명
    private String legalDongCode; // 법정동 코드
    private String legalDongName; // 법정동 명칭
    private String riName; // 리명
    private String administrativeDongName; // 행정동 명칭
    private String mountainStatus; // 산지여부
    private String landNumberMain; // 지번(주)
    private String landNumberSub; // 지번(부)
    private String oldPostalCode; // 이전 우편번호
    private String postalCodeSequenceNumber; // 우편번호 순번

    // 기본 생성자 추가 (JPA 요구사항)
    public Address() {
    }

    // 기존 생성자 및 나머지 필드에 대한 getter/setter 유지

    public Long getId() {
        return id;
    }

    // 기본적으로 id는 외부에서 변경되지 않도록 setter 제거 가능
    // public void setId(Long id) { this.id = id; }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityDistrict() {
        return cityDistrict;
    }

    public void setCityDistrict(String cityDistrict) {
        this.cityDistrict = cityDistrict;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getFullAddress() {
        return this.province + " " + this.cityDistrict + " " + this.town + " " + this.roadName;
    }
}
