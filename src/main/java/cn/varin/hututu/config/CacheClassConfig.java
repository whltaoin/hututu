package cn.varin.hututu.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheClassConfig {
    /**
     * 本地缓存类
     * @return
     */
    @Bean
    public  Cache<String, String> localCacheWithCaffeine() {
       return Caffeine.newBuilder()
                .initialCapacity(1024) // 初始可容纳 1024 个键值对
                .maximumSize(10000L) // 最大容量为 10000 个键值对
                .expireAfterWrite(5L, TimeUnit.MINUTES)//设置 “写入后过期” 策略：当一个键值对被写入缓存后，若 5 分钟内没有被再次更新，该条目会被自动移除（过期）。
                .build();
    }
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 字符串类型的redis
     * @return
     */
    @Bean
    public ValueOperations<String, String> stringValueWithRedisTemplate() {
      return stringRedisTemplate.opsForValue();
    }
}
