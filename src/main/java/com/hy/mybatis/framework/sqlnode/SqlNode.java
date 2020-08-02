package com.hy.mybatis.framework.sqlnode;

/**
 * 对sqlNode进行封装
 */
public interface SqlNode {

     void apply(DynamicContext context);

}
