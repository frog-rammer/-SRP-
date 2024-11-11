package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.MemberDTO;
import com.procuone.mit_kdt.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping("/login")
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
}
