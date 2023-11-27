package com.heima.schedule.test;

import com.heima.common.redis.CacheService;
import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Description TODO
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/27
 */
@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void test() {

        //cacheService.set("key1","value1");
        //cacheService.delete("key1");

        //cacheService.lLeftPush("list1","hello");
        //String list1 = cacheService.lRightPop("list1");
        //System.out.println(list1);

        //添加数据到zset中  分值
        cacheService.zAdd("zset_key_001","hello zset 001",1000);
        cacheService.zAdd("zset_key_001","hello zset 002",8888);
        cacheService.zAdd("zset_key_001","hello zset 003",7777);
        cacheService.zAdd("zset_key_001","hello zset 004",999999);

        Set<String> strings = cacheService.zRangeByScore("zset_key_001", 0, 8888);
        System.out.println("====="+strings);
    }

}
