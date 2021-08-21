package io.github.jorgerojasdev.libraries.flux.notifier.model.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableFluxNotifiers {
    String[] basePackages();
}
