package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: delete请求
 *
 * @author 刘磊
 * @since 2020/4/17
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@XRequest(method = XRequestMethod.DELETE)
public @interface XDeleteRequest {
    /**
     * 请求路径
     */
    String path();
}
