package com.hy.mybatis.framework.sqlsource;

public interface SqlSource {


    /**
     * 获取到可以执行的sql语句
     * @return
     */
    public BoundSql getBoundSql(Object param);
}
