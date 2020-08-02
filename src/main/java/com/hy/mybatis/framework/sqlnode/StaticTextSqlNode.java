package com.hy.mybatis.framework.sqlnode;

/**
 * 存储#{}或者没有特殊字符的文本
 */
public class StaticTextSqlNode implements SqlNode{


    private String sqlText;

    public StaticTextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }
    @Override
    public void apply(DynamicContext context) {
        context.appendSql(sqlText);
    }
}
