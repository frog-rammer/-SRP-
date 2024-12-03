package com.procuone.mit_kdt.service;

import com.procuone.mit_kdt.entity.DeliveryOrder;

import java.util.List;

public interface DeliveryOrderService {
    List<DeliveryOrder> findPendingOrders();
}