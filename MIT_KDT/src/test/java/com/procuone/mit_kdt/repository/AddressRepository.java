package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // 추가적인 쿼리 메서드가 필요하면 여기에 작성
}
