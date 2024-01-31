package com.only.plugins;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Properties;

public class PagePlugin implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    @Override
    public Object plugin(Object target) {

        if (target instanceof StatementHandler) {
            // JDK 动态代理
            return Proxy.newProxyInstance(PagePlugin.class.getClassLoader(),
                    new Class[]{StatementHandler.class},
                    new PageHandler((StatementHandler) target));
        }

        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    // 动态代理
    private class PageHandler implements InvocationHandler {

        StatementHandler handler;

        public PageHandler(StatementHandler handler) {
            this.handler = handler;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (method.getName().equalsIgnoreCase("prepare")) {
                setParamentersProxy();
            }

            return method.invoke(handler, args);
        }

        private void setParamentersProxy() {
            if (handler.getBoundSql().getParameterObject() instanceof Map) {
                ((Map) handler.getBoundSql().getParameterObject()).values().stream()
                        .filter(a -> a instanceof Page)
                        .findFirst()
                        .ifPresent(
                                page -> {
                                    appendPageSql((Page) page);
                                }
                        );
            }
        }

        private void appendPageSql(Page page) {
            try {
                BoundSql sql = handler.getBoundSql();
                sql.getSql();
                String limit = String.format(" limit %s,%s", page.getBegin(), page.getSize());
                setFieldValue("sql", sql, sql.getSql() + limit);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        private Object getFieldValue(String fieldName, Object target) throws NoSuchFieldException, IllegalAccessException {
            Field sqlField = target.getClass().getDeclaredField(fieldName);
            sqlField.setAccessible(true);
            return sqlField.get(target);
        }

        private Object setFieldValue(String fileName, Object target, Object value) throws NoSuchFieldException, IllegalAccessException {
            Field sqlField = target.getClass().getDeclaredField(fileName);
            sqlField.setAccessible(true);
            sqlField.set(target, value);
            return sqlField.get(target);
        }


    }
}
