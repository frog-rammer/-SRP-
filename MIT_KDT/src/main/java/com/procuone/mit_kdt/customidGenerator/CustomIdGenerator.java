package com.procuone.mit_kdt.customidGenerator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

public class CustomIdGenerator implements IdentifierGenerator, BeforeExecutionGenerator {

    private String prefix;    // 엔티티별 prefix
    private String tableName; // 엔티티별 테이블명
    private String columnName; // 엔티티별 컬럼명

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        // Hibernate에서 전달된 파라미터를 사용하여 prefix, tableName, columnName 초기화
        this.prefix = params.getProperty("prefix");
        this.tableName = params.getProperty("tableName");
        this.columnName = params.getProperty("columnName");
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        try {
            // 동적으로 테이블명과 컬럼명을 설정
            String sql = String.format(
                    "SELECT MAX(CAST(SUBSTRING(%s, %d) AS UNSIGNED)) AS max_id FROM %s",
                    columnName,          // 컬럼명
                    prefix.length() + 1, // prefix 길이 + 1
                    tableName            // 테이블명
            );

            Object result = session.createNativeQuery(sql).getSingleResult();

            int nextId = 1;

            if (result != null) {
                nextId = Integer.parseInt(result.toString()) + 1;
            }

            // 새로운 ID 생성: ~~ 00001 형식
            return prefix + String.format("%05d", nextId);
        } catch (Exception e) {
            throw new RuntimeException("Error generating custom ID", e);
        }
    }
}
