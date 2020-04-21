package com.liuxing.xrequest.client;

import org.springframework.beans.factory.FactoryBean;

/**
 * desc: 用于生成实体bean
 *
 * @author 刘磊
 * @since 2020/4/17
 */
public class XClientProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        return (T) new XClientProxy().bind(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
