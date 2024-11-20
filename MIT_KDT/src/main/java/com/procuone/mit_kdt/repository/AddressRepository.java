package com.procuone.mit_kdt.repository;

import com.procuone.mit_kdt.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByRoadNameContainingAndMainBuildingNumberAndSubBuildingNumber(
            String roadName, Integer mainBuildingNumber, Integer subBuildingNumber);

    List<Address> findByRoadNameContaining(String roadName);

    List<Address> findByRoadNameContainingAndMainBuildingNumber(String roadName, Integer mainBuildingNumber);
}
