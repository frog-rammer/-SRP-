package com.procuone.mit_kdt.customidGenerator;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;


public class CustomIdGenerator implements IdentifierGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        String prefix = "PdPlan_";
        try {
            // JPQL 또는 네이티브 쿼리를 사용하여 최대 ID 값을 가져옵니다
            String sql = "SELECT MAX(CAST(SUBSTRING(product_plan_code, 8) AS UNSIGNED)) AS max_id FROM production_plan";
            Query query = session.createNativeQuery(sql);
            Object result = query.getSingleResult();

            int nextId = 1;

            if (result != null) {
                nextId = Integer.parseInt(result.toString()) + 1;
            }

            // 새로운 ID 생성
            return prefix + String.format("%03d", nextId);
        } catch (Exception e) {
            throw new RuntimeException("Error generating custom ID", e);
        }
    }
}
