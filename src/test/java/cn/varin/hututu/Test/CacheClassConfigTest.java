package cn.varin.hututu.Test;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CacheClassConfigTest {
    @Resource
    private Cache<String,String> localCacheWithCaffeine;
    @Test
    public void test() {
        localCacheWithCaffeine.put("key1", "value1");
        String ifPresent = localCacheWithCaffeine.getIfPresent(("key1"));
        System.out.println(ifPresent);
    }
}
