package com.jorgerojasdev.libraries.fluxnotificator.handler;

import com.jorgerojasdev.libraries.fluxnotificator.service.implementation.NotificatorImp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NotificatorInvocationHandler<T> implements InvocationHandler {

    private NotificatorImp<T> notificatorImp;

    public NotificatorInvocationHandler(NotificatorImp<T> notificatorImp) {
        this.notificatorImp = notificatorImp;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.notificatorImp, args);
    }
}
