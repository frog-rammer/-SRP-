package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.dto.MemberDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "login";
    }

    @PostMapping("/login")
    public String loginService(){

        return "/Home";
    }
}
