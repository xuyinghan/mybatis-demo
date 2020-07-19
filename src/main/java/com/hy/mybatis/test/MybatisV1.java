package com.hy.mybatis.test;

import com.hy.mybatis.po.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class MybatisV1 {

    Properties properties = new Properties();

    @Before
    public void testBefore(){
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test() {
        Map<String,Object> map = new HashMap<>();
        map.put("username","王五");
        List<User> queryListByname = selectList("queryListByname", map);

        System.out.println(queryListByname);
    }

    private <T> List<T> selectList(String statementId,Object param){
        List<T> results = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName(properties.getProperty("db.driver"));
            connection = DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.username"), properties.getProperty("db.password"));

            String sql =  properties.getProperty("db.querysql."+statementId);
            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1,properties.getProperty("db.querysql.param"));

            if(SimpleTypeRegistry.isSimpleType(param.getClass())){
                preparedStatement.setObject(1,param);
            }else if(param instanceof Map) {
                Map map = (Map) param;
                String property = properties.getProperty("db.querysql.param");
                String[] params = property.split(",");
                for (int i = 0; i < params.length; i++) {
                    Object value = map.get(params[i]);
                    preparedStatement.setObject(i + 1, value);

                }
            }


            resultSet = preparedStatement.executeQuery();
            String returnClzName = properties.getProperty("db.querysql." + statementId + ".returnClz");
            Class<?> clz = Class.forName(returnClzName);
            Object o = null;
            while (resultSet.next()){

                 o = clz.newInstance();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {

                    String columnName = metaData.getColumnName(i);
                    Field declaredField = clz.getDeclaredField(columnName);
                    declaredField.setAccessible(true);
                    declaredField.set(o,resultSet.getObject(i));
                }
                results.add((T) o);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(resultSet!= null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(preparedStatement!= null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return  results;
    }
}
