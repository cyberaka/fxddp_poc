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
import com.cyberaka.fxddp.common.event.NewRabbitAddedEvent;
import com.cyberaka.fxddp.common.pojo.Rabbit;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.advantageous.ddp.ConnectedMessage;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.DDPMessageEndpointImpl;
import io.advantageous.ddp.JsonMessageConverter;
import io.advantageous.ddp.MessageConverter;
import io.advantageous.ddp.rpc.RPCClient;
import io.advantageous.ddp.rpc.RPCClientImpl;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.WebSocketContainer;
import java.io.IOException;
import javafx.application.Platform;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Receives domain-specific events based on crud events from the GUI.
 *
 * @author cyberaka
 */
public class RabbitsEventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitsEventReceiver.class);

    @Inject
    private EventBus eventBus;

    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    public void handleAdded(final NewRabbitAddedEvent event) {
        LOGGER.info("Received Event for new rabbit >> " + event);
        Platform.runLater(() -> {
            addDataToServer(event.getRabbit());
        });
    }

    public boolean addDataToServer(final Rabbit rabbit) {
        WebSocketContainer container = ClientManager.createClient();
        MessageConverter messageConverter = new JsonMessageConverter();

        DDPMessageEndpoint endpoint = new DDPMessageEndpointImpl(container, messageConverter);

        final RPCClient rpcClient = new RPCClientImpl(endpoint);

        endpoint.registerHandler(ConnectedMessage.class, message -> {

            try {
                rpcClient.call(Constants.RABBITS_INSERT_RPC, new Object[]{rabbit}, result -> {
                }, failureMessage -> {
                    System.out.println(failureMessage.getReason());
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        try {
            endpoint.connect(Constants.RABBITS_WEBSOCKET_URL);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
