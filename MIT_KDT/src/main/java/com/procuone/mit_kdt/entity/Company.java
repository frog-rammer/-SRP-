package com.procuone.mit_kdt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Company {
    @Id
    private String businessId;

    private String comAccount;

    private String comAdd;

    private String comEmail;

    private String comManage;

    private String comName;

    private String comPhone;
}
