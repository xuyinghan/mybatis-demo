package com.hy.mybatis.test;

import org.junit.Test;

import java.sql.*;

public class JdbcDemo {

    @Test
    public void test() {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
             connection = DriverManager.getConnection("jdbc:mysql://111.231.106.221:3306/kkb?characterEncoding=utf-8",
                    "kkb", "kkb111111");

            String sql = "select  * from  user where  username = ?";
             preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,"王五");

             resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.print(resultSet.getString("id")+","+resultSet.getString("username"));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
    }
}
