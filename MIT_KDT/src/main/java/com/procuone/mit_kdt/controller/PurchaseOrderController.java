package com.procuone.mit_kdt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchaseOrder")
public class PurchaseOrderController {
    @GetMapping("/progressInspection")
    public String progressInspection() { return "purchaseOrder/progressInspection"; }

    @GetMapping("/purchaseOrders")
    public String purchaseOrders() { return "purchaseOrder/purchaseOrders"; }

    @GetMapping("/procurementPlanView")
    public String procurementPlanView() { return "purchaseOrder/procurementPlanView"; }
}
