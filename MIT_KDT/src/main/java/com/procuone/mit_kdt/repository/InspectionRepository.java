package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Inspection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionRepository extends JpaRepository<Inspection, String> {
    Page<Inspection> findByInspectionStatus(String inspectionStatus, Pageable pageable);
}