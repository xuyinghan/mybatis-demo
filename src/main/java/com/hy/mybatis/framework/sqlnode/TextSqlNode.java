package com.hy.mybatis.framework.sqlnode;

import com.hy.mybatis.framework.util.GenericTokenParser;
import com.hy.mybatis.framework.util.OgnlUtils;
import com.hy.mybatis.framework.util.SimpleTypeRegistry;
import com.hy.mybatis.framework.util.TokenHandler;

/**
 * 存储${}的sql文本
 */
public class TextSqlNode implements SqlNode{

    private String sqlText;

    public TextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    public boolean isDynamic(){
        if(sqlText.indexOf("${")>-1){
            return true;
        }
        return false;
    }

    @Override
    public void apply(DynamicContext context) {
        BindingTokenHandler tokenHandler = new BindingTokenHandler(context);
        GenericTokenParser tokenParser = new GenericTokenParser("${","}",tokenHandler);
        String parseSql = tokenParser.parse(sqlText);
    }

    class BindingTokenHandler implements TokenHandler{

        private DynamicContext context;

        public BindingTokenHandler(DynamicContext context) {
            this.context = context;
        }

        /**
         * 返回${}中的参数值
         * @param content
         * @return
         */
        @Override
        public String handleToken(String content) {
            //判断入参类型，如果简单类型直接返回对应
            Object parameter = context.getBindings().get("_parameter");
            if(parameter == null){
                return "";
            }else if(SimpleTypeRegistry.isSimpleType(parameter.getClass())){
//               如果只有一个简单类型的参数，不需要关注${}里的内容是什么
                return parameter.toString();
            }
//            如果不是简单类型，是Map或者对象，需要通过ognl表达式获取

            Object value = OgnlUtils.getValue(content, parameter);


            return value == null?"":value.toString();
        }
    }
}
