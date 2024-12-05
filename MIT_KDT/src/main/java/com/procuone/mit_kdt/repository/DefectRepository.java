package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {
}
