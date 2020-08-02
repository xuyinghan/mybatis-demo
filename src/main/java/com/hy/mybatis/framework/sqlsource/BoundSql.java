package com.hy.mybatis.framework.sqlsource;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BoundSql {

    // jdbc可以执行sql语句
    private String sql;

    // 参数list
    private List<ParameterMapping> mappingList;


    public BoundSql(String sql,  List<ParameterMapping> mappingList ){
        this.sql = sql;
        this.mappingList = mappingList==null?new ArrayList<>():mappingList;
    }

    public void addParameterMapping(ParameterMapping parameterMapping){
        this.mappingList.add(parameterMapping);
    }
}
