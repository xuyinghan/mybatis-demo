package com.hy.mybatis.test;

import com.hy.mybatis.framework.config.Configuration;
import com.hy.mybatis.framework.config.MappedStatement;
import com.hy.mybatis.framework.sqlnode.*;
import com.hy.mybatis.framework.sqlsource.BoundSql;
import com.hy.mybatis.framework.sqlsource.ParameterMapping;
import com.hy.mybatis.framework.sqlsource.SqlSource;
import com.hy.mybatis.framework.sqlsource.support.DynamicSqlSource;
import com.hy.mybatis.framework.sqlsource.support.RawSqlSource;
import com.hy.mybatis.po.User;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class MybatisV2<T> {

    private  Configuration configuration = new Configuration();
    String namespace;
    boolean isDynamic;
    @Before
    public void testBefore(){
        
    }

    private void loadXML(String path) {
        Document document = loadXmlDocument(path);
        parseConfiguration(document.getRootElement());
    }

    private Document loadXmlDocument(String path) {
        InputStream inputStream = getResourceAsStream(path);
        return createDocument(inputStream);
    }

    private void parseConfiguration(Element rootElement) {
        Element environments = rootElement.element("environments");
        parseEnvironment(environments);
        Element mappers = rootElement.element("mappers");
        parseMappers(mappers);

    }

    private void parseMappers(Element mappers) {
        List<Element> elements = mappers.elements();
        for (Element element:elements){
            String resourcePath = element.attributeValue("resource");
            loadMapperXML(resourcePath);
        }
    }

    private void loadMapperXML(String resourcePath) {

        Document document = loadXmlDocument(resourcePath);
        parseMapper(document.getRootElement());
    }

    private void parseMapper(Element rootElement) {
         namespace = rootElement.attributeValue("namespace");
        java.util.List<Element> elements = rootElement.elements();
        for (Element element :elements){
           parseStatement(element);
        }
    }

    private void parseStatement(Element element) {
        String id = element.attributeValue("id");
        if(StringUtils.isEmptyOrWhitespaceOnly(id)){
            return;
        }
        String statementId = namespace+"."+id;
        String parameterType = element.attributeValue("parameterType");
        Class<?> clazz = resolveClass(parameterType);
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClazz = resolveClass(parameterType);
        String statementType = element.attributeValue("statementType");

        if(StringUtils.isEmptyOrWhitespaceOnly(statementType)){
            statementType ="prepared";
        }

        SqlSource sqlSource = createSqlSource(element);

    }

    private SqlSource createSqlSource(Element element) {

        SqlSource sqlSource =parseScriptNode(element);

        return sqlSource;
    }

    private SqlSource parseScriptNode(Element element) {
//        解析所有的sqlNode
        MixedSqlNode mixSqlNode = parseDynamicTags(element);
        SqlSource sqlSource = null;

        //如果包含动态标签或者${},那么用DynamicSqlSource
        if(isDynamic){
            sqlSource = new DynamicSqlSource(mixSqlNode);
        }else{
            sqlSource = new RawSqlSource(mixSqlNode);

        }
        return sqlSource;
    }

    private MixedSqlNode parseDynamicTags(Element element) {

        List<SqlNode> sqlNodes = new ArrayList<>();
        int nodeCount = element.nodeCount();
        for(int i =0;i<nodeCount;i++){
            Node node = element.node(i);
            if(node instanceof Text){
                String text = node.getText().trim();
                if (StringUtils.isNullOrEmpty(text)) {

                    continue;
                }
                TextSqlNode sqlNode = new TextSqlNode(text);
                if(sqlNode.isDynamic()){
                    isDynamic = true;
                    sqlNodes.add(sqlNode);
                }else {
                    sqlNodes.add( new StaticTextSqlNode(text));
                }
            }else if(node instanceof  Element){
                isDynamic = true;
                Element elementNode = (Element) node;
                String elementName = elementNode.getName();
                if("if".equals(elementName)){
                    String test = elementNode.attributeValue("test");
                    MixedSqlNode mixedSqlNode = parseDynamicTags(elementNode);
                    IfSqlNode ifSqlNode = new IfSqlNode(test,mixedSqlNode);
                    sqlNodes.add(ifSqlNode);
                }else if("where".equals(elementName)){

                }
            }
        }
        return new MixedSqlNode(sqlNodes);
    }

    private Class resolveClass(String parameterType) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(parameterType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
        }
        return clazz;
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
            parseDataSource(dataSource);
        });

    }

    private void parseDataSource(Element dataSource) {
        String type = dataSource.attributeValue("type");

        if("DBCP".equals(type)){
            BasicDataSource ds = new BasicDataSource();
            Properties properties = parseProperties(dataSource);
            ds.setDriverClassName(properties.getProperty("driver"));
            ds.setPassword(properties.getProperty("password"));
            ds.setUrl(properties.getProperty("url"));
            ds.setUsername(properties.getProperty("username"));
            configuration.setDataSource(ds);
        }


    }

    private Properties parseProperties(Element dataSource) {
        Properties properties = new Properties();
        Iterator property = dataSource.elementIterator("property");
        while (property.hasNext()){
            Element next = (Element) property.next();
            properties.setProperty(next.attributeValue("name"),next.attributeValue("value"));
        }
        return properties;
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
        Map<String,Object> map = new HashMap<>();
        map.put("username","王五");
        List<User> users = selectList("test.findUserById",map);

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
            //获取sql
//           根据ID获取映射的statement语句
            MappedStatement mappedStatementById = configuration.getMappedStatementById(statementId);

            SqlSource sqlSource = mappedStatementById.getSqlSource();
            BoundSql boundSql = sqlSource.getBoundSql(param);
            String sql = boundSql.getSql();
            if(mappedStatementById.getStatementType().equals("prepared")) {
                //获取statement对象
                preparedStatement = connection.prepareStatement(sql);
//                设置参数
                setParamters(
                        preparedStatement,param,boundSql);
//            查询
                rs = preparedStatement.executeQuery();
//            处理结果集
            handleResultSet(rs,results,mappedStatementById);

            }else if(mappedStatementById.getStatementType().equals("callable")){

            }else{

            }

//
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
        return  results;
    }


    private <T> void handleResultSet(ResultSet resultSet,List<T> results,MappedStatement mappedStatement) throws  Exception{

        Class<?> clz = mappedStatement.getResultTypeClass();
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
    }

    private void setParamters(PreparedStatement preparedStatement, Object param, BoundSql boundSql) {

        try {
            if (SimpleTypeRegistry.isSimpleType(param.getClass())) {
                preparedStatement.setObject(1, param);
            } else if (param instanceof Map) {
                Map map = (Map) param;

//                String paramnames = properties.getProperty("db.sql." + statementId + ".paramnames");
//                Class<?> parameterTypeClass = mappedStatementById.getParameterTypeClass();

                List<ParameterMapping> mappingList = boundSql.getMappingList();
//                Field[] fields = parameterTypeClass.getFields();
//                String[] Stringnames = paramnames.split(",");
                for (int i = 0; i < mappingList.size(); i++) {
                    Object value = map.get(mappingList.get(i).getName());
                    preparedStatement.setObject(i + 1, value);
                }

            } else {
                // TODO 暂时不处理
            }
        } catch (Exception throwables) {

        } finally {
        }
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
