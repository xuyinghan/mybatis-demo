package com.hy.mybatis.framework.sqlnode;

import java.util.List;

/**
 * 存储同一级别的sql文本
 */
public class MixedSqlNode implements SqlNode{

    private List<SqlNode> sqlNodeList;

    public MixedSqlNode(List<SqlNode> sqlNodeList) {
        this.sqlNodeList = sqlNodeList;
    }

    @Override
    public void apply(DynamicContext context) {

        for (SqlNode sqlNode : sqlNodeList){

            sqlNode.apply(context);
        }
    }
}
