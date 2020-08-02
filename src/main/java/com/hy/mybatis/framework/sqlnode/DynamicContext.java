package com.hy.mybatis.framework.sqlnode;


import java.util.HashMap;
import java.util.Map;

/**
 * sqlNode处理过程中的上下文对象
 */
public class DynamicContext {

    /**
     * 把sqlNode处理之后的信息拼成一条完整的sql语句
     * */
    private StringBuffer sb = new StringBuffer();

    /**
     * sqlNode执行过程中需要的一些信息
     */
    private Map<String,Object> bindings = new HashMap<>();

    public DynamicContext(Object param) {
        this.bindings.put("_parameter",param);
    }

    public String getSqlString(){
        return sb.toString();
    }

    public void appendSql(String sqlText){
        this.sb.append(sqlText);
        this.sb.append(" ");
    }


    public Map<String, Object> getBindings() {
        return bindings;
    }

    public void addBindings(String name, Object binding) {
        this.bindings.put(name,binding);
    }
}
