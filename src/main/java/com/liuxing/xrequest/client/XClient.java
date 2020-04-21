package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc:请求
 *
 * @author 刘磊
 * @since 2020/4/17
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XClient {
    /**
     * 地址配置
     */
    String base();
}
