package com.liuxing.xrequest.client;

import com.liuxing.xrequest.util.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * desc: 将生成的接口实体bean注册到spring容器中
 *
 * @author 刘磊
 * @since 2020/4/17
 */
@Component
public class XClientRegistryBean implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    private ApplicationContext ctx;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // 通过配置指定请求客户端名路径
        String basepackage = ctx.getEnvironment().getProperty("xclient_basepackage");
        if (basepackage == null || basepackage.length() == 0) {
            return;
        }
        List<Class<?>> classList = ClassUtil.getClassListByAnnotation(basepackage, XClient.class);
        for (Class<?> cls : classList) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
            definition.setBeanClass(XClientProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            // 注册bean名,一般为类名首字母小写
            String clsName = cls.getSimpleName();
            String beanName = clsName.substring(0, 1).toLowerCase() + clsName.substring(1);
            beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
