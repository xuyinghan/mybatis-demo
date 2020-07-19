package com.hy.mybatis.framework.config;

import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装xml的配置
 */
@Data
public class Configuration {

    private DataSource dataSource;

    private Map<String,MappedStatement> mappedStatements = new HashMap<>();



    public MappedStatement getMappedStatementById(String statementId) {
        return mappedStatements.get(statementId);
    }

    public void addMappedStatement(String statementId, MappedStatement mappedStatement) {
        this.mappedStatements.put(statementId, mappedStatement);
    }

}
