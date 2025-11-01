package cn.varin.hututu.annotation;

import java.lang.annotation.*;

/**
 * 自动检查权限注解,
 * 应用于方法,保留到运行时
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCheckRole {
    String roleValue() default "";
}
