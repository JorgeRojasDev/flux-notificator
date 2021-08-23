package io.github.jorgerojasdev.libraries.flux.notifier.config;

import io.github.jorgerojasdev.libraries.flux.notifier.handler.NotifierInvocationHandler;
import io.github.jorgerojasdev.libraries.flux.notifier.service.abstraction.Notifier;
import io.github.jorgerojasdev.libraries.flux.notifier.service.implementation.NotifierImp;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class TestFluxNotifierExecutionListener implements TestExecutionListener, Ordered {

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        injectDependencies(testContext);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    protected void injectDependencies(TestContext testContext) throws Exception {
        Object bean = testContext.getTestInstance();
        Class<?> clazz = testContext.getTestClass();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) testContext.getApplicationContext().getAutowireCapableBeanFactory();

        for (Field declaredField : clazz.getDeclaredFields()) {
            Type[] interfaces = declaredField.getType().getGenericInterfaces();
            for (Type ifc : interfaces) {
                Class fieldClass = Class.forName(declaredField.getType().getTypeName());
                ParameterizedType ifcType = ((ParameterizedType) ifc);
                Class ifcClass = Class.forName(ifcType.getRawType().getTypeName());
                if (ifcClass.equals(Notifier.class) && ifcClass.isInterface()) {
                    Type[] arguments = ifcType.getActualTypeArguments();
                    assert arguments.length > 0 : "Argument of Notifier must be declared";
                    Type argument = arguments[0];
                    Object proxyFluxNotifier = Proxy.newProxyInstance(fieldClass.getClassLoader(),
                            new Class[]{fieldClass},
                            new NotifierInvocationHandler<>(
                                    new NotifierImp<>(Class.forName(argument.getTypeName()))
                            ));
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxyFluxNotifier.getClass());
                    beanDefinitionBuilder.addConstructorArgValue(Proxy.getInvocationHandler(proxyFluxNotifier));
                    beanFactory.registerBeanDefinition(ifc.getTypeName(), beanDefinitionBuilder.getBeanDefinition());
                }
            }
        }
        beanFactory.autowireBeanProperties(bean, 0, false);
    }
}
