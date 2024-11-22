package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // Item 엔티티의 productCode를 기준으로 계약 목록을 조회하는 쿼리 메서드
    List<Contract> findByItemProductCode(String productCode);

}
