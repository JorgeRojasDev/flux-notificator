package io.github.jorgerojasdev.libraries.flux.notifier.service.abstraction;

import io.github.jorgerojasdev.libraries.flux.notifier.model.exception.ChannelNotFoundException;
import io.github.jorgerojasdev.libraries.flux.notifier.model.exception.ConsumerNotFoundException;
import reactor.core.publisher.Flux;

import java.util.function.Predicate;

public interface Notifier<T> {

    Flux<T> addConsumer(String channel, Object consumerId);

    void removeConsumer(String channel, Object consumerId) throws ChannelNotFoundException, ConsumerNotFoundException;

    void emitAll(String channel, T notification) throws ChannelNotFoundException;

    void emitSingle(String channel, Object consumerId, T notification) throws ChannelNotFoundException, ConsumerNotFoundException;

    void emitWhen(String channel, Predicate<Object> condition, T notification) throws ChannelNotFoundException;
}
