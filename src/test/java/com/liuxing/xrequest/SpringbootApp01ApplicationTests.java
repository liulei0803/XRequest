package com.liuxing.xrequest;

import org.junit.Test;

public class SpringbootApp01ApplicationTests {

    @Test
    public void contextLoads() {
        String base = "${test}/pro/${bbb}";
        while (base.contains("${") && base.contains("}")) {
            String prop = base.split("\\$\\{")[1].split("}")[0];
            base = base.replace("${" + prop + "}", "xxx");
        }
        System.out.println(base);
    }

}
