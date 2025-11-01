package cn.varin.hututu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Spring MVC Json 配置
 * 解决Long类型转JSON时的精度丢失问题
 */
@JsonComponent
public class JsonConfig {

    /**
     * 配置ObjectMapper，注册Long类型转字符串的序列化器
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 创建ObjectMapper并禁用XML映射
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 新建模块，用于注册自定义序列化器
        SimpleModule module = new SimpleModule();
        // 为Long包装类和long基本类型注册“转字符串”序列化器
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 将模块注册到ObjectMapper
        objectMapper.registerModule(module);
        return objectMapper;
    }
}