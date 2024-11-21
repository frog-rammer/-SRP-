package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.dto.CompanyItemDTO;
import com.procuone.mit_kdt.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
}