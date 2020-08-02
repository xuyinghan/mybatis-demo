package com.hy.mybatis.framework.sqlsource.support;

import com.hy.mybatis.framework.sqlnode.DynamicContext;
import com.hy.mybatis.framework.sqlnode.SqlNode;
import com.hy.mybatis.framework.sqlsource.BoundSql;
import com.hy.mybatis.framework.sqlsource.SqlSource;
import com.hy.mybatis.framework.util.GenericTokenParser;
import com.hy.mybatis.framework.util.ParameterMappingTokenHandler;

/**
 * 封装并解析非动态标签和 #{}相关的SQL信息
 * #{}仅被解析一次
 */
public class RawSqlSource implements SqlSource {

    //封装解析出来的sqlNode信息
    private SqlNode rootSqlNode;

    private SqlSource sqlSource;

    public RawSqlSource(SqlNode rootSqlNode){
      this.rootSqlNode = rootSqlNode;

        //        解析所有的sqlnode拼成一条语句
        DynamicContext context = new DynamicContext(null);
        rootSqlNode.apply(context);
//        此时语句中可能有#{},还需要对#{}进行解析
        String sql = context.getSqlString();


        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{","}",tokenHandler);
        //        解析成可执行的sql语句

        String parseSql = tokenParser.parse(sql);

//        将sql语句和解析#{}产生的参数列表信息封装成boundsql
        sqlSource = new StaticeSqlSource(sql,tokenHandler.getParameterMappings());
    }
    @Override
    public BoundSql getBoundSql(Object param) {
        return sqlSource.getBoundSql(param);
    }
}
