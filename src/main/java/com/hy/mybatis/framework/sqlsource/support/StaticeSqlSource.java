package com.hy.mybatis.framework.sqlsource.support;

import com.hy.mybatis.framework.sqlsource.BoundSql;
import com.hy.mybatis.framework.sqlsource.ParameterMapping;
import com.hy.mybatis.framework.sqlsource.SqlSource;

import java.util.List;

/**
 * 封装dynamicsqlsource 和 rawsqlsource 的数据
 */
public class StaticeSqlSource implements SqlSource {
    // jdbc可以执行sql语句
    private String sql;
    // 参数list
    private List<ParameterMapping> mappingList;

    @Override
    public BoundSql getBoundSql(Object param) {
        return new BoundSql(sql,mappingList);
    }

    public StaticeSqlSource(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.mappingList = parameterMappings;
    }
}
