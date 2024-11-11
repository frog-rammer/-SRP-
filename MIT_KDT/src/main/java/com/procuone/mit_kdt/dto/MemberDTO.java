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
    private LocalDate creationDate = LocalDate.now();
}
