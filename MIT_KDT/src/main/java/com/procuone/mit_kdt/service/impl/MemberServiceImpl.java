package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.entity.Member;
import com.procuone.mit_kdt.repository.MemberRepository;
import com.procuone.mit_kdt.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(String memberId, String password) {

        System.out.println("아이디 :" + memberId);

        // 유저 아이디로 사용자 검색
        Optional<Member> memberOptional = memberRepository.findByMemberId(memberId);
        if (!memberOptional.isPresent()) {
            return "failure";
        }

        Member member = memberOptional.get();
        if (passwordEncoder.matches(password, member.getPassword())) {
            return "success";
        } else {
            return "failure";
        }
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

    @Override
    public String signup(MemberDTO dto) {
        // 아이디 중복 체크
        if (isMemberIdExists(dto.getMemberId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);


        Member entity = convertToEntity(dto);
        memberRepository.save(entity); // DB 저장 시 creationDate 자동 설정

        return entity.getMemberId(); // 가입된 회원의 ID 반환
    }

    // 회원 아이디 중복 확인 메서드 추가
    @Override
    public boolean isMemberIdExists(String memberId) {
        return memberRepository.existsByMemberId(memberId);  // 존재 여부를 확인
    }
}
