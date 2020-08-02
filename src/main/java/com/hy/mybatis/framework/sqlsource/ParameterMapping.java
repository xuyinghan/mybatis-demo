package com.hy.mybatis.framework.sqlsource;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParameterMapping {

    private String name;

    private Class type;

    public  ParameterMapping(String name,Class type){
        this.name = name;
        this.type = type;
    }

    public  ParameterMapping(String name){
        this.name = name;
    }
}
