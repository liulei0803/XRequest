package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: 用于在方法上指定请求头信息
 *
 * @author 刘磊
 * @since 2020/4/21
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRequestHeader {
    /**
     * 请求头的key值
     */
    String value();
}
