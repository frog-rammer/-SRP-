package com.procuone.mit_kdt.controller;

import com.procuone.mit_kdt.service.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/materialReceipt")
public class MaterialReceiptController {


    @Autowired
    DeliveryOrderService deliveryOrderService;

    @GetMapping("/stockIn")
    public String stockIn() {
        return "material/stockIn";
    }
}