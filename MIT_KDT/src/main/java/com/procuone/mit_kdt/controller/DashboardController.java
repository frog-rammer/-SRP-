package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aaa")
public class DashboardController {
    @GetMapping("/bbb")
    public String productionPartView(Model model) {
        return  "dashboard/productpartdashboard";
    }
    @GetMapping("/ccc")
    public String productionPartView2(Model model) {
        return  "dashboard/procurementdashboard";
    }
}
