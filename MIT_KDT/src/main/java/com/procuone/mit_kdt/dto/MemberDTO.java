package com.procuone.mit_kdt.dto;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String memberId;
    private String memberName;
    private String password;
    private String email;
    private String phone;
    private String Dno;
    private String type;
    private LocalDate creationDate = LocalDate.now();

    // 매개변수를 받는 생성자
    public MemberDTO(String username, String memberName, String userType) {
        this.memberName = username;
        this.memberName = memberName;
        this.type = userType;
    }
}
