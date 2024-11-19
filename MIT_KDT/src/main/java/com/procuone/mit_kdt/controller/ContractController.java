package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contract")
public class ContractController {


    @GetMapping("/register")
    public String register() {
        return "procurementPlan/compareContracts";
    }

    @GetMapping("/registerContract")
    public String registerContract() {
        return "procurementPlan/registerContract";
    }

    @GetMapping("/registerContract2")
    public String registerContract2(RedirectAttributes redirectAttributes) {
        return "redirect:/contract/register";
    }
}
