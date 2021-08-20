package com.jorgerojasdev.libraries.fluxnotificator.config;

import com.jorgerojasdev.libraries.fluxnotificator.model.annotation.EnableFluxNotifiers;
import com.jorgerojasdev.libraries.fluxnotificator.processor.FluxNotifierProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class FluxNotifierBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        configurableListableBeanFactory.getBeansWithAnnotation(EnableFluxNotifiers.class).forEach((name, beanObject) -> {
            try {
                for (String basePackage : configurableListableBeanFactory.findAnnotationOnBean(name, EnableFluxNotifiers.class).basePackages()) {
                    FluxNotifierProcessor.process(beanDefinitionRegistry, basePackage);
                }
            } catch (NoSuchBeanDefinitionException e) {
                e.printStackTrace();
            }
        });
    }
}
