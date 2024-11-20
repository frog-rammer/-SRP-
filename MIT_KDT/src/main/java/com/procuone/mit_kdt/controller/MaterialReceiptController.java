package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/materialReceipt")
public class MaterialReceiptController {

    @GetMapping("/invoice")
    public String invoice() {
        return "materialReceipt/invoice";
    }

    @GetMapping("/stockIn")
    public String stockIn() {
        return "materialReceipt/stockIn";
    }

    @GetMapping("/prodCosts")
    public String prodCosts() {return "materialReceipt/prodCosts";}

    @GetMapping("/inspectionStatus")
    public String inspectionStatus() {return "materialReceipt/inspectionStatus";}
}
