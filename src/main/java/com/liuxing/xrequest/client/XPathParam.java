package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: 请求参数
 * 当请求地址是rest风格时， 需要通过此注解注明参数与rest中的占位符对应关系
 *
 * @author 刘磊
 * @since 2020/4/20
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XPathParam {
    /**
     * 请求路径
     */
    String value();
}
