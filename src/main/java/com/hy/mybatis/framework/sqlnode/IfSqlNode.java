package com.hy.mybatis.framework.sqlnode;

import com.hy.mybatis.framework.util.OgnlUtils;

/**
 * 存储if标签的sql文本
 */
public class IfSqlNode implements SqlNode{

    private String text;

    private SqlNode mixedSqlNode;

    public IfSqlNode(String text, SqlNode mixedSqlNode) {
        this.text = text;
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public void apply(DynamicContext context) {

        boolean parameter = OgnlUtils.evaluateBoolean(text, context.getBindings().get("_parameter"));
        if(parameter){
            mixedSqlNode.apply(context);
        }
    }
}
