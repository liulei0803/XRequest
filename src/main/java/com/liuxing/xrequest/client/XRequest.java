package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: 请求
 *
 * @author 刘磊
 * @since  2020/4/17
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XRequest {
    /**
     * 请求路径
     */
    String path() default "";

    /**
     * 请求类型
     */
    XRequestMethod method();
}
