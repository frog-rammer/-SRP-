package com.procuone.mit_kdt.scheduler;

import com.procuone.mit_kdt.service.ContractService;
import com.procuone.mit_kdt.service.DeliveryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerComponent {

    @Autowired
    private ContractService contractService;
    @Autowired
    private DeliveryOrderService deliveryOrderService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedRate = 6000000)
    public void deleteExpiredContracts() {
        contractService.deleteExpiredContracts();
    }

//    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
     @Scheduled(fixedRate = 6000000)
    public void updateExpiredContracts() {
        deliveryOrderService.updateDeliveryStatus();
    }
}
