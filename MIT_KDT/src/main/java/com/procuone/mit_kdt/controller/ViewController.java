package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String login() { return "login"; }

    @GetMapping("/signup")
    public String signup() { return "signup"; }

    @GetMapping("/registerCompanyForm")
    public String registerCompanyForm() { return "registerCompanyForm"; }

    @GetMapping("/registerProductForm")
    public String registerProductForm() { return "registerProductForm"; }

    @GetMapping("/productList")
    public String productList() { return "productList"; }

    @GetMapping("/productionPlanView")
    public String productionPlanInfo() { return "productionPlanView"; }

    @GetMapping("/productionPlanInput")
    public String productionPlanInput() { return "productionPlanInput"; }

}
