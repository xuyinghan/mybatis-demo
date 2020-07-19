package com.hy.mybatis.test;

import com.hy.mybatis.framework.config.Configuration;
import com.hy.mybatis.framework.config.MappedStatement;
import com.hy.mybatis.po.User;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;


import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class MybatisV2 {

    private  Configuration configuration = new Configuration();

    @Before
    public void testBefore(){
        
    }

    private void loadXML(String path) {
       InputStream inputStream = getResourceAsStream(path);
       Document document = createDocument(inputStream);
       parseConfiguration(document.getRootElement());
    }

    private void parseConfiguration(Element rootElement) {
        Element environments = rootElement.element("environments");
        parseEnvironment(environments);
        Element mappers = rootElement.element("mappers");
        parseMappers(mappers);

    }

    private void parseMappers(Element mappers) {
    }

    private void parseEnvironment(Element environments) {
        String str =  environments.attributeValue("default");
        List<Element> environment = environments.elements("environment");
        environment.forEach(element -> {
            String id = element.attributeValue("id");
            if(!id.equals(str)){
              return;
            }
            Element dataSource = element.element("dataSource");
            dataSource.elements("property");
        });

    }


    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            return saxReader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return  null;
    }




    private InputStream getResourceAsStream(String path) {
       return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    @Test
    public void test(){
        // 加载XML文件，将数据封装到Configuration对象中
        loadXML("mybatis-config.xml");
        // 执行用户查询操作
        // 根据用户名称查询列表信息
        List<User> users = selectList("queryUserById","王五");

        System.out.println(users);
    }
    private <T> List<T> selectList(String statementId,Object param) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        List<T> results = new ArrayList<>();
        try {
            // 获取连接
           connection = getConnection();
            
           String sql =  getSql();

           preparedStatement = connection.prepareStatement(sql);
            MappedStatement mappedStatementById = configuration.getMappedStatementById(statementId);
            setParamters(
                    preparedStatement,param,mappedStatementById);
            rs = preparedStatement.executeQuery();
            handleResultSet(rs,mappedStatementById.getResultTypeClass());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private void handleResultSet(ResultSet rs, Class<?> resultTypeClass) {
    }

    private void setParamters(PreparedStatement preparedStatement, Object param, MappedStatement mappedStatementById) {

        if (SimpleTypeRegistry.isSimpleType(param.getClass())) {
            preparedStatement.setObject(1, param);
        } else if (param instanceof Map) {
            Map map = (Map) param;

            String paramnames = properties.getProperty("db.sql." + statementId + ".paramnames");
            String[] names = paramnames.split(",");
            for (int i = 0; i < names.length; i++) {
                Object value = map.get(names[i]);
                preparedStatement.setObject(i + 1, value);
            }

        } else {
            // TODO 暂时不处理
        }
    }

    private String getSql() {
        return null;
    }

    private Connection getConnection() {
        try {
            DataSource dataSource = configuration.getDataSource();
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return  null;
    }
}
