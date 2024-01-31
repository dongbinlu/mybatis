package com.only.test;

import com.only.entity.User;
import com.only.mapper.UserMapper;
import com.only.plugins.Page;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MybatisApp {
    SqlSessionFactory sqlSessionFactory = null;
    SqlSession session = null;

    @Before
    public void before() throws Exception {
        //将XML配置文件构建为Configuration配置类
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        // SqlSessionFactoryBuilder 构建会话工厂，基于mybatis.config.xml,构建完成后即可丢弃。
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        // SqlSessionFactory 用于生成会话的工厂，作用于整个应用运行期间，不需要构建多个，一个会话工厂即可。

        // SqlSesson 作用于单次会话，如web一次请求期间，不能作用于某个对象的一个属性，也不能在多个线程间共享，因为它是线程不安全的。
        session = sqlSessionFactory.openSession(true);
    }

    @Test
    public void test() {
        UserMapper userMapper = session.getMapper(UserMapper.class);
        User user = userMapper.getByUserId(1);
        System.out.println(user);
    }


    /**
     * 测试一级缓存
     */
    @Test
    public void testLocalCache() {

        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user1 = userMapper.getByUserId(1);

        User user2 = userMapper.getByUserId(1);

        System.out.println(user1 == user2);
    }

    /**
     * 测试二级缓存
     * <p>
     * 如果两个namespace对同一张表进行操作，是否会导致当前namespace的二级缓存失效，答案 不失效
     */
    @Test
    public void testCache() throws Exception {


        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user1 = userMapper.getByUserId(1);

        // 会提交二级缓存
        session.close();

        TimeUnit.SECONDS.sleep(1);

        Thread t = new Thread(() -> {
            UserMapper mapper2 = sqlSessionFactory.openSession(true).getMapper(UserMapper.class);
            User user2 = mapper2.getByUserId(1);

            /**
             * 只读readOnly）属性可以被设置为 true 或 false。
             * 只读的缓存会给所有调用者返回缓存对象的相同实例。
             * 因此这些对象不能被修改。这就提供了可观的性能提升。
             * 而可读写的缓存会（通过序列化）返回缓存对象的拷贝。
             * 速度上会慢一些，但是更安全，因此默认值是 false
             */
            System.out.println(user1 == user2);
        });

        t.start();
        t.join();


    }


    @Test
    public void testConcurrentCache() throws Exception {
        UserMapper mapper1 = session.getMapper(UserMapper.class);
        UserMapper mapper2 = session.getMapper(UserMapper.class);
        System.out.println("mapper1 == mapper2 : " + (mapper1 == mapper2));// false 注意 这个是不一样的，因为每次都会创建一个新的动态代理对象
        User user1 = mapper1.getByUserId(1);
        User user2 = mapper2.getByUserId(1); // user1和user2是相同的，因为存入到1级缓存了。

        System.out.println("user1 == user2 : " + (user1 == user2));

         /*
         如果另一个session并发修改数据，1级缓存还是会生效，不同的session有不通的缓存空间
          */
        Thread thread1 = new Thread(() -> {
            System.out.println("inner执行了");
            SqlSession sqlSession = sqlSessionFactory.openSession();
            UserMapper mapper3 = sqlSession.getMapper(UserMapper.class);
            User user3 = mapper3.getByUserId(1);

            mapper3.updateByUserId("rrrrrrrrr", 1);
            sqlSession.commit();
            System.out.println("执行结束");
        });
        thread1.start();
        thread1.join();


        User user4 = mapper1.getByUserId(1);

        System.out.println("user1 == user2 : " + (user1 == user2));
        System.out.println("user1 == user4 : " + (user1 == user4));


        // 只有关闭会话后才会进行二级缓存
        session.close();

        /**
         * MyBatis的二级缓存是应用程序级别的缓存，它会缓存数据库查询结果，提高应用程序的性能。
         * 在使用MyBatis二级缓存时，可能会出现从缓存中取出来的对象地址不一样的情况，
         * 这是因为MyBatis缓存中缓存的是反序列化后的对象，而不是原始对象。
         * 当从缓存中获取对象时，MyBatis会新建一个实例，并将缓存中反序列化得到的对象属性值复制到新建的对象中
         * ，因此，新建的对象的内存地址是不同的，但是属性值是相同的。
         */
        Thread thread2 = new Thread(() -> {
            UserMapper mapper5 = sqlSessionFactory.openSession(true).getMapper(UserMapper.class);
            User user5 = mapper5.getByUserId(1);

            System.out.println(user2 == user5);
        });

        thread2.start();
        thread2.join();


    }

    @Test
    public void testBatchInsertByBatch() {

        sqlSessionFactory.getConfiguration().setDefaultExecutorType(ExecutorType.BATCH);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);


        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {

            User user = User.builder()
                    .username(UUID.randomUUID().toString())
                    .build();

            userMapper.saveUser(user);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start));// 1665

        sqlSession.commit();


    }

    @Test
    public void testBatchInsertBySimple() {

        sqlSessionFactory.getConfiguration().setDefaultExecutorType(ExecutorType.SIMPLE);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);


        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {

            User user = User.builder()
                    .username(UUID.randomUUID().toString())
                    .build();

            userMapper.saveUser(user);
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start));// 12128

        sqlSession.commit();


    }

    /**
     * 动态SQL
     */
    @Test
    public void testUpdate(){
        UserMapper userMapper = session.getMapper(UserMapper.class);
        userMapper.updateByUserId("jojo", 1);
        session.commit();
    }

    @Test
    public void testPage() throws Exception {
        UserMapper mapper = session.getMapper(UserMapper.class);
        List<User> list = mapper.getByUsernameTPage("jojo", new Page(1, 2));
        System.out.println(list);
    }

}
