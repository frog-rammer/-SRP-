package com.procuone.mit_kdt.scheduler;

import com.procuone.mit_kdt.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContractExpirationScheduler {

    @Autowired
    private ContractService contractService;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredContracts() {
        contractService.deleteExpiredContracts();
    }
}
