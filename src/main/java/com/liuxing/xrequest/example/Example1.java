package com.liuxing.xrequest.example;

import com.liuxing.xrequest.client.XClient;
import com.liuxing.xrequest.client.XGetRequest;
import com.liuxing.xrequest.client.XPathParam;
import com.liuxing.xrequest.client.XRequestHeader;

@XClient(base = "http://172.21.51.53:8084/worker-module/provider")
public interface Example1 {
    @XGetRequest(path = "/workerInfo/workerChiefsByOrg/{orgId}")
    Object testGet(@XPathParam("orgId") String a, @XRequestHeader("token") String token);
}
