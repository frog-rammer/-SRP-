package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    @Override
    public String login(String MemberId, String password) {

        System.out.println("아이디 :" + MemberId);

        // 유저 아이디로 사용자 검색
        Optional<Member> memberOptional = memberRepository.findByMemberId(MemberId);
        Member member = memberOptional.get();
        MemberDTO memberDTO = convertToDTO(member);

        if(memberDTO.getMemberId().equals(MemberId) && memberDTO.getPassword().equals(password)) {
            return "success";
        }else{
            return "failure";
        }

//        // 유저가 존재하는지 확인
//        if (memberOptional.isPresent()) {
//            Member member = memberOptional.get();
//
//            // 비밀번호 검증
//            if (!passwordEncoder.matches(password, member.getPassword())) {
//                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
//            } else {
//                // 아이디와 비밀번호가 맞지 않으면 인증 실패
//                throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
//            }
//
//        }

    }



    private Member convertToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .memberId(memberDTO.getMemberId())
                .memberName(memberDTO.getMemberName())
                .password(memberDTO.getPassword())
                .phone(memberDTO.getPhone())
                .email(memberDTO.getEmail())
                .Dno(memberDTO.getDno())
                .creationDate(memberDTO.getCreationDate())
                .build();
    }

    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .password(member.getPassword())
                .phone(member.getPhone())
                .email(member.getEmail())
                .Dno(member.getDno())
                .creationDate(member.getCreationDate())
                .build();
    }

}
