package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.CompanyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyItemRepository extends JpaRepository<CompanyItem, Long> {
}
