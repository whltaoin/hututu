package cn.varin.hututu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(value =  "cn.varin.hututu.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class HututuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HututuApplication.class, args);
    }

}
