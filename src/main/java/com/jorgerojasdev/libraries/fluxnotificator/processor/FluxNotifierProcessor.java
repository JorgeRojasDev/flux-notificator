package com.jorgerojasdev.libraries.fluxnotificator.processor;

import com.jorgerojasdev.libraries.fluxnotificator.handler.NotificatorInvocationHandler;
import com.jorgerojasdev.libraries.fluxnotificator.service.abstraction.Notificator;
import com.jorgerojasdev.libraries.fluxnotificator.service.implementation.NotificatorImp;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Set;

public class FluxNotifierProcessor {

    private static Logger logger = LoggerFactory.getLogger(FluxNotifierProcessor.class);

    public static void process(BeanDefinitionRegistry beanDefinitionRegistry, String basePackage) {
        Reflections ref = new Reflections(basePackage);
        Set<Class<? extends Notificator>> classes = ref.getSubTypesOf(Notificator.class);

        classes.forEach((clazz) -> {
            if (clazz.isInterface()) {
                Type[] types = clazz.getGenericInterfaces();
                if (types.length == 1) {
                    ParameterizedType type = (ParameterizedType) types[0];
                    Type[] classArguments = type.getActualTypeArguments();
                    if (classArguments.length > 0) {
                        try {
                            Object proxyFluxNotifier = Proxy.newProxyInstance(clazz.getClassLoader(),
                                    new Class[]{clazz},
                                    new NotificatorInvocationHandler<>(
                                            new NotificatorImp<>(Class.forName(classArguments[0].getTypeName())
                                            )));
                            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxyFluxNotifier.getClass());
                            beanDefinitionBuilder.addConstructorArgValue(Proxy.getInvocationHandler(proxyFluxNotifier));
                            beanDefinitionRegistry.registerBeanDefinition(clazz.getName(), beanDefinitionBuilder.getBeanDefinition());

                        } catch (Exception e) {
                            logger.error(String.format("Error Registering Bean From: %s", clazz.getCanonicalName()), e);
                        }
                    }
                }
            }
        });
    }
}
