/*
 * Copyright (c) 2008, 2014, Techworks Technologies Pvt Ltd and/or its affiliates 
 * henceforth referred to as Techworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Techworks or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.cyberaka.fxddp.net;

import com.cyberaka.fxddp.common.Constants;
import com.cyberaka.fxddp.common.event.RabbitsAddedEvent;
import com.cyberaka.fxddp.common.event.RabbitsRemovedEvent;
import com.cyberaka.fxddp.common.event.RabbitsUpdatedEvent;
import com.cyberaka.fxddp.common.pojo.Rabbit;
import com.google.common.eventbus.EventBus;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.subscription.message.AddedBeforeMessage;
import io.advantageous.ddp.subscription.message.AddedMessage;
import io.advantageous.ddp.subscription.message.ChangedMessage;
import io.advantageous.ddp.subscription.message.RemovedMessage;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

import static io.advantageous.ddp.DDPMessageHandler.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dispatch domain-specific events based on subscription events from the server.
 *
 * @author cyberaka
 */
public class SubscriptionEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionEventDispatcher.class);

    private final Map<String, SubscriptionEventHandler> handlerMap = new HashMap<>();

    @Inject
    public SubscriptionEventDispatcher(final EventBus eventBus,
            final DDPMessageEndpoint endpoint,
            final @Named("Local Data Map") Map<String, Map<String, Object>> dataMap) {

        this.register(Constants.RABBITS_COLLECTION_NAME, new SubscriptionEventHandler() {
            @Override
            public void handleAdded(final String key) {
                LOGGER.info(key);
                eventBus.post(new RabbitsAddedEvent(key, (Rabbit) dataMap.get(Constants.RABBITS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleChanged(final String key) {
                LOGGER.info(key);
                eventBus.post(new RabbitsUpdatedEvent(key, (Rabbit) dataMap.get(Constants.RABBITS_COLLECTION_NAME).get(key)));
            }

            @Override
            public void handleRemoved(final String key) {
                LOGGER.info(key);
                eventBus.post(new RabbitsRemovedEvent(key));
            }
        });

        endpoint.registerHandler(AddedMessage.class, Phase.AFTER_UPDATE, this::handleAdded);
        endpoint.registerHandler(AddedBeforeMessage.class, Phase.AFTER_UPDATE, this::handleAddedBefore);
        endpoint.registerHandler(ChangedMessage.class, Phase.AFTER_UPDATE, this::handleChanged);
        endpoint.registerHandler(RemovedMessage.class, Phase.AFTER_UPDATE, this::handleRemoved);

    }

    private void register(final String collection, final SubscriptionEventHandler handler) {
        handlerMap.put(collection, handler);
    }

    public void handleAdded(final AddedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) {
            handler.handleAdded(message.getId());
        }
    }

    public void handleAddedBefore(final AddedBeforeMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) {
            handler.handleAdded(message.getId());
        }
    }

    public void handleChanged(final ChangedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) {
            handler.handleChanged(message.getId());
        }
    }

    public void handleRemoved(final RemovedMessage message) {
        final SubscriptionEventHandler handler = this.handlerMap.get(message.getCollection());
        if (handler != null) {
            handler.handleRemoved(message.getId());
        }
    }

    public interface SubscriptionEventHandler {

        void handleAdded(String key);

        void handleChanged(String key);

        void handleRemoved(String key);
    }

}
