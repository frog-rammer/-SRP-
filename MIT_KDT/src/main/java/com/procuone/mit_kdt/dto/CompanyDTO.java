package com.procuone.mit_kdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO {
    private String businessId;
    private String comAccount;
    private String comAdd;
    private String comEmail;
    private String comManage;
    private String comName;
    private String comPhone;
}
