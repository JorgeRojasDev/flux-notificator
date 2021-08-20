package com.jorgerojasdev.libraries.fluxnotificator.service.implementation;

import com.jorgerojasdev.libraries.fluxnotificator.model.exception.ChannelNotFoundException;
import com.jorgerojasdev.libraries.fluxnotificator.model.exception.ConsumerNotFoundException;
import com.jorgerojasdev.libraries.fluxnotificator.service.abstraction.Notificator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class NotificatorImp<T> implements Notificator<T> {

    private Map<String, Map<Object, Sinks.Many<T>>> consumers = new HashMap<>();
    private Map<String, Sinks.Many<T>> channels = new HashMap<>();
    private Logger log;

    public NotificatorImp(Class<T> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    @Override
    public Flux<T> addConsumer(String channel, Object consumerId) {
        addConsumerToCollection(channel, consumerId);
        addChannelToCollection(channel);
        return channels.get(channel).asFlux().mergeWith(consumers.get(channel).get(consumerId).asFlux());
    }

    @Override
    public void removeConsumer(String channel, Object consumerId) throws ChannelNotFoundException, ConsumerNotFoundException {
        assertChannelAndConsumerExists(channel, consumerId);
        consumers.get(channel).get(consumerId).tryEmitComplete();
        consumers.get(channel).remove(consumerId);
    }

    @Override
    public void emitAll(String channel, T notification) throws ChannelNotFoundException {
        assertChannelExists(channel);
        consumers.get(channel).forEach((consumerId, emitter) -> {
            if (emitter.tryEmitNext(notification).isFailure()) {
                log.warn("Error emitting notification to: {} -> id: {}", consumerId, notification);
            }
        });
    }

    @Override
    public void emitSingle(String channel, Object consumerId, T notification) throws ChannelNotFoundException, ConsumerNotFoundException {
        assertChannelAndConsumerExists(channel, consumerId);
        if (consumers.get(channel).get(consumerId).tryEmitNext(notification).isFailure()) {
            log.warn("Error emitting notification -> to: {} -> id: {}", consumerId, notification);
        }
    }

    @Override
    public void emitWhen(String channel, Predicate<Object> condition, T notification) throws ChannelNotFoundException {
        assertChannelExists(channel);
        consumers.get(channel).forEach((consumerId, emitter) -> {
            if (condition.test(consumerId)) {
                if (emitter.tryEmitNext(notification).isFailure()) {
                    log.warn("Error emitting notification -> to: {} -> id: {}", consumerId, notification);
                }
            }
        });
    }

    private boolean channelExists(String channel) {
        assert channel != null;
        return consumers.containsKey(channel) && channels.containsKey(channel);
    }

    private void assertChannelExists(String channel) throws ChannelNotFoundException {
        if (!channelExists(channel)) {
            throw new ChannelNotFoundException("Channel doesn't exist");
        }
    }

    private void assertChannelAndConsumerExists(String channel, Object consumerId) throws ChannelNotFoundException, ConsumerNotFoundException {
        assertChannelExists(channel);
        assert consumerId != null;
        if (!consumers.get(channel).containsKey(consumerId)) {
            throw new ConsumerNotFoundException("Consumer doesn't exist");
        }
    }

    private void addConsumerToCollection(String channel, Object consumerId) {
        if (channelExists(channel)) {
            consumers.get(channel).putIfAbsent(consumerId, Sinks.many().unicast().onBackpressureBuffer());
        } else {
            Map<Object, Sinks.Many<T>> map = new HashMap<>();
            map.put(consumerId, Sinks.many().unicast().onBackpressureBuffer());
            consumers.put(channel, map);
        }
    }

    private void addChannelToCollection(String channel) {
        channels.putIfAbsent(channel, Sinks.many().multicast().onBackpressureBuffer());
    }
}
