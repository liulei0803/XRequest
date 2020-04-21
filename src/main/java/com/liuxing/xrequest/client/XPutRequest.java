package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: Put请求方式
 *
 * @author 刘磊
 * @since  2020/4/17
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@XRequest(method = XRequestMethod.PUT)
public @interface XPutRequest {
    /**
     * 请求路径
     */
    String path();
}
