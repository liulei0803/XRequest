package com.liuxing.xrequest.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HttpClient Util类
 *
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    private static final RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(500).setSocketTimeout(80000).build();

    private static final CloseableHttpClient httpClient;

    /**
     * HTTP保留时间
     */
    private static final int MAX_HTTP_KEEP_ALIVE = 48 * 1000;

    /**
     * 最大连接数
     */
    private static final int MAX_TOTAL_CONNECTIONS = 800;

    /**
     * 每个路由最大连接数
     */
    private static final int MAX_ROUTE_CONNECTIONS = 400;

    /**
     * 获取连接超时时间
     */
    private static final int CONNECT_TIMEOUT = 5000;

    /**
     * 连接处理超时时间
     * 供应链接请求太长
     */
    private static final int SOCKET_TIMEOUT = 80000;

    private static final String CHARSET = "UTF-8";

    static {
        HttpRequestRetryHandler myRetryHandler = (exception, executionCount, context) -> false;
        ConnectionKeepAliveStrategy customKeepAliveHandler = (response, context) -> MAX_HTTP_KEEP_ALIVE;
        connManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        httpClient = HttpClients.custom().setConnectionManager(connManager).setKeepAliveStrategy(customKeepAliveHandler)
                .setRetryHandler(myRetryHandler).build();
    }

    public static String doGet(String url) throws IOException {
        return doGet(url, CHARSET, null, null);
    }

    public static String doGet(String url, String charset) throws IOException {
        return doGet(url, charset, null, null);
    }

    public static String doGetWithToken(String url, String token) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);
        return doGet(url, CHARSET, null, headerMap);
    }

    public static String doGetWithParam(String url, Map<String, Object> paramMap) throws IOException {
        return doGet(url, CHARSET, paramMap, null);
    }

    public static String doGetWithHeader(String url, Map<String, String> headerMap) throws IOException {
        return doGet(url, CHARSET, null, headerMap);
    }

    /**
     * 以get方式请求指定的http服务
     *
     * @param url       地址
     * @param charset   编码
     * @param paramMap  参数
     * @param headerMap 请求头
     */
    public static String doGet(String url, String charset, Map<String, Object> paramMap, Map<String, String> headerMap) throws IOException {
        logger.info("get请求地址: {}, 参数：{}", url, paramMap);
        // 将请求参数拼接到get请求地址后面
        String address = dealGetParam(url, paramMap);
        // 创建get请求
        HttpGet httpGet = new HttpGet(address);
        // 添加额外的请求头信息
        setHeader(httpGet, headerMap);
        // 设置连接超时配置
        httpGet.setConfig(requestConfig);
        // 执行请求并且返回响应结果
        return executeHttpRequest(httpGet, charset);
    }

    public static String doPost(String url, String content) throws IOException {
        return doPost(url, content, CHARSET, ContentType.APPLICATION_JSON, null);
    }

    public static String doPostWithToken(String url, String content, String token) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("token", token);
        return doPost(url, content, CHARSET, ContentType.APPLICATION_JSON, headerMap);
    }

    public static String doPostWithHeader(String url, String content, Map<String, String> headerMap) throws IOException {
        return doPost(url, content, CHARSET, ContentType.APPLICATION_JSON, headerMap);
    }

    /**
     * 以post方式请求指定的http服务
     *
     * @param url         http url
     * @param content     请求体
     * @param charset     字符编码(如UTF8)
     * @param contentType 内容格式(如application/json)
     * @param headerMap   请求头键值
     * @return 响应结果
     */
    public static String doPost(String url, String content, String charset, ContentType contentType, Map<String, String> headerMap) throws IOException {
        logger.info("post请求地址：{} , 请求参数：{}", url, content);
        HttpPost httpPost = new HttpPost(url);
        setHeader(httpPost, headerMap);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        StringEntity reqEntity = new StringEntity(content, contentType);
        httpPost.setEntity(reqEntity);
        return executeHttpRequest(httpPost, charset);
    }

    /**
     * 处理get请求的参数拼接
     *
     * @param url      请求地址
     * @param paramMap 参数
     */
    private static String dealGetParam(String url, Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        for (Map.Entry<String, Object> entry: paramMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.append("a=1");
        return sb.toString();
    }

    /**
     * 设置请求头信息
     *
     * @param request   请求
     * @param headerMap 请求头信息
     */
    private static void setHeader(HttpUriRequest request, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            for (Entry<String, String> entry : headerMap.entrySet()) {
                String key = entry.getKey();
                if (key == null || StringUtils.isEmpty(key)) {
                    continue;
                }
                String val = headerMap.get(key);
                request.addHeader(key, val != null ? val : "");
            }
            // 模拟浏览器请求
            headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");
        }
    }

    /**
     * 执行http请求
     *
     * @param request 请求
     * @param charset 编码
     */
    private static String executeHttpRequest(HttpUriRequest request, String charset) throws IOException {
        CloseableHttpResponse response = null;
        try {
            long start = System.currentTimeMillis();
            response = httpClient.execute(request);
            long end = System.currentTimeMillis();
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (statusCode != HttpStatus.SC_OK) {
                if (entity != null) {
                    logger.warn("请求失败({}ms):statusCode={},resp={}", end - start, statusCode, EntityUtils.toString(entity, charset));
                }
                return "";
            }
            String resp = EntityUtils.toString(entity, charset);
            logger.info("请求响应({}ms):{}", end - start, resp);
            return resp;
        } catch (ClientProtocolException e) {
            logger.error("请求协议异常：", e);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
        return "";
    }
}
