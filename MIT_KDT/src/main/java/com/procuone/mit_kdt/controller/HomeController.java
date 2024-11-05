package com.procuone.mit_kdt.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HomeController {

    @GetMapping("/Home")
    public String Home() {

        return "home";
    }
}
