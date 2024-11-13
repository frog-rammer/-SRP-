package com.procuone.mit_kdt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/materialIssue")
public class MaterialIssueController{

    @GetMapping("/stock")
    public String stock() {
        return "materialIssue/stock";
    }

    @GetMapping("/stockOut")
    public String stockOut() {
        return "materialIssue/stockOut";
    }
}
