package com.liuxing.xrequest;

import com.alibaba.fastjson.JSON;
import com.liuxing.xrequest.example.Example1;
import com.liuxing.xrequest.example.Example2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MyController {

    @Autowired
    private Example1 example1;
    @Autowired
    private Example2 example2;

    @GetMapping(path = "/sayHello")
    public String getHello(HttpServletRequest request){
        Object getRes = example1.testGet("9c2bd44d32ab4872b245c4266bbe4704", request.getHeader("token"));
        return "hello world: " + JSON.toJSONString(getRes);
    }

    @PostMapping(path = "/sayHello")
    public String postHello(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        map.put("chiefId", "c7bc66b6ce4b4bdcb894e52f4565e208");
        map.put("salecluesId", "1000245289");
        Object postRes = example2.testPost(map, request.getHeader("token"));
        return "hello world!" + JSON.toJSONString(postRes);
    }
}
