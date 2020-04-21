package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: get请求
 *
 * @author 刘磊
 * @since 2020/4/17
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@XRequest(method = XRequestMethod.GET)
public @interface XGetRequest {
    /**
     * 请求路径
     */
    String path();
}
