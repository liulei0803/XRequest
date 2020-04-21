package com.liuxing.xrequest.example;

import com.liuxing.xrequest.client.XClient;
import com.liuxing.xrequest.client.XPostRequest;
import com.liuxing.xrequest.client.XRequestBody;
import com.liuxing.xrequest.client.XRequestHeader;

import java.util.Map;

/**
 * 通过占位符方式配置调用端地址
 */
@XClient(base = "${remote_url}/provider")
public interface Example2 {
    @XPostRequest(path = "/workerInfo/getContractPayWorkers")
    Object testPost(@XRequestBody Map<String, Object> param, @XRequestHeader("token") String token);
}
