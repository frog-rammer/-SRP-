package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contract")
public class ContractController {
    @GetMapping("/register")
    public String register() {
        return "procurementPlan/registerContract";
    }
}
