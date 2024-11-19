package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping({"/","/login"})
    public String login(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginService(@ModelAttribute MemberDTO memberDTO, HttpSession session) {

        //로그인 시도
        String ok=memberService.login(memberDTO.getMemberId(), memberDTO.getPassword());

        if(ok.equals("success")) {
            return "/Home";
        }else{
            return "/login";
        }
    }

    @PostMapping("/signup")
    public String signup(MemberDTO dto, RedirectAttributes redirectAttributes) {
        String memberId = memberService.signup(dto);
        redirectAttributes.addFlashAttribute("memberDTO", dto);
        return "redirect:/login";
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkId(@RequestParam String memberId) {
        // memberService에서 중복 아이디 확인
        boolean exists = memberService.isMemberIdExists(memberId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    // 회원가입 페이지 요청 처리
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());  // 빈 MemberDTO 객체를 뷰에 전달
        return "signup";  // 회원가입 폼을 렌더링
    }


}