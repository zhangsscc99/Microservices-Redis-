package com.heima.schedule.test;


import com.alibaba.fastjson.JSON;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.sun.istack.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Set;




@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void testList(){

        //在list的左边添加元素
//        cacheService.lLeftPush("list_001","hello,redis");

        //在list的右边获取元素，并删除
        String list_001 = cacheService.lRightPop("list_001");
        System.out.println(list_001);
    }

    @Test
    public void testZset(){
        //添加数据到zset中  分值
        /*cacheService.zAdd("zset_key_001","hello zset 001",1000);
        cacheService.zAdd("zset_key_001","hello zset 002",8888);
        cacheService.zAdd("zset_key_001","hello zset 003",7777);
        cacheService.zAdd("zset_key_001","hello zset 004",999999);*/

        //按照分值获取数据
        Set<String> zset_key_001 = cacheService.zRangeByScore("zset_key_001", 0, 8888);
        System.out.println(zset_key_001);

        //可以按照分值获取数据。
        //
    }

    @Test
    public void testKeys(){
        Set<String> keys = cacheService.keys("future_*");
        System.out.println(keys);

        Set<String> scan = cacheService.scan("future_*");
        System.out.println(scan);
    }

    //耗时6151
    @Test
    public  void testPiple1(){
        long start =System.currentTimeMillis();
        for (int i = 0; i <10000 ; i++) {
            Task task = new Task();
            task.setTaskType(1001);
            task.setPriority(1);
            task.setExecuteTime(new Date().getTime());
            cacheService.lLeftPush("1001_1", JSON.toJSONString(task));
        }
        System.out.println("耗时"+(System.currentTimeMillis()- start));
    }


    @Test
    public void testPiple2(){
        long start  = System.currentTimeMillis();
        //使用管道技术
        List<Object> objectList = cacheService.getstringRedisTemplate().executePipelined(new RedisCallback<Object>() {
            @Nullable
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                for (int i = 0; i <10000 ; i++) {
                    Task task = new Task();
                    task.setTaskType(1001);
                    task.setPriority(1);
                    task.setExecuteTime(new Date().getTime());
                    redisConnection.lPush("1001_1".getBytes(), JSON.toJSONString(task).getBytes());
                }
                return null;
            }
        });
        System.out.println("使用管道技术执行10000次自增操作共耗时:"+(System.currentTimeMillis()-start)+"毫秒");
    }

}


//延迟任务 没有明确开始时间。由事件触发。
//在这个事件触发之后的一段时间内触发另一个事件。
//任务可以立即执行 也可以延迟。
//没有支付则取消订单。
//接口对接出现网络问题。1分钟后重试。
//网络问题。
//delay queue。阻塞队列。
//任务放在内存里。程序挂掉之后，任务放在内存，消息丢失。如何保证数据不丢失。
//rabbitmq实现延迟任务。
//time to live。
//dead letter exchange。 消息成为dead message之后，可以重新发送另一个交换机。
//设置队列的过期时间。
//消息队列 pop出来。双端队列 执行效率更加高。
//立即执行的任务。 未来数据队列定时刷新到当前消费队列。
//list存储立即执行的任务，zset存储未来的数据。
//任务量过大后，zset的性能会下降。
//对数级复杂度。执行时间随着数据规模增长的变化趋势。
//zadd 时间复杂度 O(M*log(n))
//常量级别复杂度 执行次数和数据规模没有关系。
//list命令lpush。
//添加zset 为什么需要预先加载
//任务数据特别大 为了防止阻塞 把未来几分钟要执行的数据存入缓存即可。是一种优化的形式。
//添加zset数据。