package com.jorgerojasdev.libraries.fluxnotificator.service.abstraction;

import com.jorgerojasdev.libraries.fluxnotificator.model.exception.ChannelNotFoundException;
import com.jorgerojasdev.libraries.fluxnotificator.model.exception.ConsumerNotFoundException;
import reactor.core.publisher.Flux;

import java.util.function.Predicate;

public interface Notificator<T> {

    Flux<T> addConsumer(String channel, Object consumerId);

    void removeConsumer(String channel, Object consumerId) throws ChannelNotFoundException, ConsumerNotFoundException;

    void emitAll(String channel, T notification) throws ChannelNotFoundException;

    void emitSingle(String channel, Object consumerId, T notification) throws ChannelNotFoundException, ConsumerNotFoundException;

    void emitWhen(String channel, Predicate<Object> condition, T notification) throws ChannelNotFoundException;
}
