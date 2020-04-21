package com.liuxing.xrequest.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuxing.xrequest.util.ApplicationContextUtil;
import com.liuxing.xrequest.util.HttpClientUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * desc: 通过动态代理方式为接口生成实例
 *
 * @author 刘磊
 * @since 2020/4/17
 */
public class XClientProxy implements InvocationHandler {
    private Class<?> interfaceClass;

    public Object bind(Class<?> cls) {
        this.interfaceClass = cls;
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 取出URL的前缀部分
        XClient xClient = interfaceClass.getAnnotation(XClient.class);
        if (xClient == null) {
            return null;
        }
        String base = xClient.base();
        if (base.length() == 0) {
            return null;
        }
        // 需要读取配置的情况
        while (base.contains("${") && base.contains("}")) {
            String prop = base.split("\\$\\{")[1].split("}")[0];
            String propVal = ApplicationContextUtil.getProperty(prop);
            base = base.replace("${" + prop + "}", propVal);
        }
        // 请求路径
        String path = null;
        // 方法请求类型
        XRequestMethod methodType = null;
        // 根据各种请求类型获取路径和请求类型
        XRequest xRequest = method.getAnnotation(XRequest.class);
        if (xRequest != null) {
            path = xRequest.path();
            methodType = xRequest.method();
        } else {
            // GET
            XGetRequest xGetRequest = method.getAnnotation(XGetRequest.class);
            if (xGetRequest != null) {
                path = xGetRequest.path();
                methodType = XRequestMethod.GET;
            }
            // POST
            XPostRequest xPostRequest = method.getAnnotation(XPostRequest.class);
            if (xPostRequest != null) {
                path = xPostRequest.path();
                methodType = XRequestMethod.POST;
            }
            // PUT
            XPutRequest xPutRequest = method.getAnnotation(XPutRequest.class);
            if (xPutRequest != null) {
                path = xPutRequest.path();
                methodType = XRequestMethod.PUT;
            }
            // DELETE
            XDeleteRequest xDeleteRequest = method.getAnnotation(XDeleteRequest.class);
            if (xDeleteRequest != null) {
                path = xDeleteRequest.path();
                methodType = XRequestMethod.DELETE;
            }
        }
        if (path == null) {
            return null;
        }
        // 将地址前缀部分与路径拼接成URL
        String url = concatPath(base, path);
        // 获取参数上的注解
        Annotation[][] annotations = method.getParameterAnnotations();
        // 请求体，post和put会有
        Object requestBody = null;
        // 请求头
        Map<String, String> headers = new HashMap<>();
        // 解析参数注解
        for (int i = 0; i < annotations.length; i++) {
            Annotation[] ans = annotations[i];
            for (Annotation an : ans) {
                // 通过注解匹配参数与占位符
                if (an instanceof XPathParam) {
                    XPathParam xPathParam = (XPathParam) an;
                    url = url.replace("{" + xPathParam.value() + "}", args[i].toString());
                }
                // 请求体
                if (an instanceof XRequestBody) {
                    requestBody = args[i];
                }
                // 请求头
                if (an instanceof XRequestHeader) {
                    XRequestHeader header = (XRequestHeader) an;
                    headers.put(header.value(), args[i].toString());
                }
            }
        }
        // 返回类型
        Class<?> returnType = method.getReturnType();
        // http请求结果
        String res;
        // 发送get请求
        if (methodType == XRequestMethod.GET) {
            if (requestBody != null) {
                url = url + "?" + getParam(requestBody);
            }
            if (headers.size() > 0) {
                res = HttpClientUtil.doGetWithHeader(url, headers);
            } else {
                res = HttpClientUtil.doGet(url);
            }
            try {
                // 解析响应结果
                return JSON.parseObject(res, returnType);
            } catch (Exception e) {
                // 无法解析表示返回结果是基本类型
                return res;
            }
        }
        // 发送POST请求
        if (methodType == XRequestMethod.POST) {
            String param = new JSONObject().toJSONString();
            if (requestBody != null) {
                param = JSON.toJSONString(requestBody);
            }
            if (headers.size() > 0) {
                res = HttpClientUtil.doPostWithHeader(url, param, headers);
            } else {
                res = HttpClientUtil.doPost(url, param);
            }
            try {
                // 解析响应结果
                return JSON.parseObject(res, returnType);
            } catch (Exception e) {
                // 无法解析表示返回结果是基本类型
                return res;
            }
        }
        //TODO put请求和delete请求处理省略
        return null;
    }

    /**
     * 拼接地址与路径
     *
     * @param base 远端地址
     * @param path 请求路径
     */
    private String concatPath(String base, String path) {
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return (base + path);
    }

    private String getParam(Object jsonObj) {
        JSONObject json = JSON.parseObject(JSON.toJSONString(jsonObj));
        StringBuilder sb = new StringBuilder();
        json.keySet().forEach(key -> {
            Object val = json.get(key);
            if (val instanceof JSONObject || val instanceof JSONArray) {
                return;
            }
            sb.append(key).append("=").append(val.toString()).append("&");
        });
        return sb.substring(0, sb.length() - 1);
    }
}
