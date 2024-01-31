package com.only.mapper;

import com.only.entity.User;
import com.only.plugins.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@CacheNamespace(
//        implementation= PerpetualCache.class, //缓存实现 Cache接口实现类
//        eviction= LruCache.class,// 缓存算法
//        flushInterval=60000, // 刷新时间，单位：毫秒；这里的刷新是指缓存数据的有效期
//        size=1024,   // 最大缓存引用对象
//        readWrite=true, // 是否可写,也就是是否需要进行拷贝，true:必须实现序列化接口
//        blocking=false  // 是否阻塞，防止缓存击穿。
//)

public interface UserMapper {

    User getByUserId(@Param("userId") Integer userId);

    void updateByUserId(@Param("username") String username, @Param("userId") Integer userId);

    User getByUsername(@Param("username") String username);

    @Select("select * from user where username = #{username}")
    List<User> getByUsernameTPage(@Param("username") String username, Page page);

    void saveUser(User user);


}
