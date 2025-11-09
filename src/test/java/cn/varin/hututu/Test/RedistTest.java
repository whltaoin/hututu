package cn.varin.hututu.Test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class RedistTest {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void test() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("hello", "world");
        String hello = stringStringValueOperations.get("hello");
        log.info("hello = {}", hello);

    }
}
