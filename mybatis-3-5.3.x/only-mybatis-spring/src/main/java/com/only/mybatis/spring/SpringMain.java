package com.only.mybatis.spring;

import com.only.mybatis.spring.entity.User;
import com.only.mybatis.spring.mapper.UserMapper;
import com.only.mybatis.spring.service.UserService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class SpringMain {

    @Test
    public void testSimple() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
        UserMapper userMapper = ctx.getBean(UserMapper.class);

        User user = userMapper.getByUserId(1);

        System.out.println(user);

    }

    @Test
    public void testPlus() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-plus.xml");
        UserMapper userMapper = ctx.getBean(UserMapper.class);

        User user = userMapper.getByUserId(1);

        System.out.println(user);

    }


    @Test
    public void testSpringMybatis() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

        UserService userService = ctx.getBean(UserService.class);

        userService.getUser(1);

        userService.getUser(1);

    }

    /**
     * 手动开启事务
     *
     * @throws Exception
     */
    @Test
    public void testHMT() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
        // 1、获取事务管理器
        DataSourceTransactionManager tm = ctx.getBean(DataSourceTransactionManager.class);

        // 2、获取事务定义
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        // 3、设置事务隔离级别
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        // 4、获得事务状态
        TransactionStatus ts = tm.getTransaction(def);


        UserService userService = ctx.getBean(UserService.class);

        try {

            userService.getUser2(1);

        } catch (Exception e) {
            tm.rollback(ts);
            throw e;
        }
        tm.commit(ts);
    }

    /**
     * 开启事务@Transactional
     *
     * @throws Exception
     */
    @Test
    public void testSpringTransactional() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

        UserService userService = ctx.getBean(UserService.class);

        userService.getUser3(1);

    }

    /**
     * 开启事务@Transactional-做新增
     *
     * @throws Exception
     */
    @Test
    public void testSpringTransactionalToAdd() throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

        UserService userService = ctx.getBean(UserService.class);

        userService.addUser("baidu");

    }

}
