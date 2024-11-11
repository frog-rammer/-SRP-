package com.procuone.mit_kdt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String email;
    private String phone;
    private LocalDate creationDate = LocalDate.now();
}
