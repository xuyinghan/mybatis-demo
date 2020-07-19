package com.hy.mybatis.framework.config;

import com.hy.mybatis.framework.sqlsource.SqlSource;
import lombok.Data;

@Data
public class MappedStatement {


    private String statementId;
    private SqlSource sqlSource;

    private String statementType;

    private Class<?> parameterTypeClass;
    private Class<?> resultTypeClass;

    public MappedStatement(String statementId, Class<?> parameterTypeClass, Class<?> resultTypeClass,
                           String statementType, SqlSource sqlSource) {
        this.statementId = statementId;
        this.parameterTypeClass = parameterTypeClass;
        this.resultTypeClass = resultTypeClass;
        this.statementType = statementType;
        this.sqlSource = sqlSource;
    }

}
