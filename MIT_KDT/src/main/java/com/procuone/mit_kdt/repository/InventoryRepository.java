package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * 특정 Item ID에 대한 현재 재고 수량을 가져오기
     */
    @Query("SELECT i.currentQuantity FROM Inventory i WHERE i.item.id = :itemId")
    Integer findCurrentQuantityByItemId(@Param("itemId") Long itemId);

    /**
     * 특정 Item ID에 대한 예약된 수량을 가져오기
     */
    @Query("SELECT i.reservedQuantity FROM Inventory i WHERE i.item.id = :itemId")
    Integer findReservedQuantityByItemId(@Param("itemId") Long itemId);

    /**
     * 특정 Item ID에 대한 최소 유지 수량을 가져오기
     */
    @Query("SELECT i.minimumRequired FROM Inventory i WHERE i.item.id = :itemId")
    Integer findMinimumRequiredByItemId(@Param("itemId") Long itemId);


    Optional<Inventory> findByItemId(Long itemId);
}
