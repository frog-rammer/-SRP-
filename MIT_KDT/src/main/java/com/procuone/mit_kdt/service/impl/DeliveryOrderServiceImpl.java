package com.procuone.mit_kdt.service.impl;

import com.procuone.mit_kdt.entity.DeliveryOrder;
import com.procuone.mit_kdt.entity.Inspection;
import com.procuone.mit_kdt.repository.DeliveryOrderRepository;
import com.procuone.mit_kdt.repository.InspectionRepository;
import com.procuone.mit_kdt.service.DeliveryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

    @Autowired
    private final DeliveryOrderRepository deliveryOrderRepository;

    @Autowired
    private final InspectionRepository inspectionRepository;

    @Override
    public List<DeliveryOrder> findPendingOrders() {
        // "완료" 상태인 납품 데이터를 조회
        List<DeliveryOrder> completedOrders = deliveryOrderRepository.findByStatusAndDeliveryDateBefore("완료", LocalDate.now());

        // Inspection 테이블에 데이터 추가
        completedOrders.forEach(order -> {
            Inspection inspection = Inspection.builder()
                    .deliveryOrder(order)
                    .inspectionDate(LocalDate.now()) // 현재 날짜를 검수일로 설정
                    .productName(order.getProductCode()) // 상품명
                    .quantity(order.getDeliveryQuantity()) // 납품 수량
                    .defectiveQuantity(0L) // 초기 불량 갯수는 0
                    .inspectionStatus("검수 중") // 기본 검수 상태는 "검수 중"
                    .build();

            inspectionRepository.save(inspection);
        });

        return completedOrders; // 반환된 데이터를 필요에 따라 사용
    }
}

