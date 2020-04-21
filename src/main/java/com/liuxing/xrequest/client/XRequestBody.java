package com.liuxing.xrequest.client;

import java.lang.annotation.*;

/**
 * desc: 用于指定此参数是请求体信息
 *
 * @author 刘磊
 * @since 2020/4/20
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRequestBody {
}
