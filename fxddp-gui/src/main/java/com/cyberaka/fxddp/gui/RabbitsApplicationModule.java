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
package com.cyberaka.fxddp.gui;

import com.cyberaka.fxddp.gui.impl.MainViewController;
import com.cyberaka.fxddp.net.RabbitsEventReceiver;
import com.cyberaka.fxddp.net.SubscriptionEventDispatcher;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.DDPMessageEndpointImpl;
import io.advantageous.ddp.JsonMessageConverter;
import io.advantageous.ddp.MessageConverter;
import io.advantageous.ddp.subscription.JsonObjectConverter;
import io.advantageous.ddp.subscription.MapSubscriptionAdapter;
import io.advantageous.ddp.subscription.ObjectConverter;
import io.advantageous.ddp.subscription.SubscriptionAdapter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.websocket.WebSocketContainer;
import org.glassfish.tyrus.client.ClientManager;

/**
 * Guice based module implementation that defines the binding between various
 * parts of the application.
 *
 * @author cyberaka
 */
class RabbitsApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RabbitsApplication.class);

        bind(MessageConverter.class).to(JsonMessageConverter.class).in(Singleton.class);
        bind(ObjectConverter.class).to(JsonObjectConverter.class).in(Singleton.class);
        bind(SubscriptionAdapter.class).to(MapSubscriptionAdapter.class).in(Singleton.class);
        bind(DDPMessageEndpoint.class).to(DDPMessageEndpointImpl.class).in(Singleton.class);
        bind(SubscriptionEventDispatcher.class).asEagerSingleton();
        bind(RabbitsEventReceiver.class).asEagerSingleton();
        bind(EventBus.class).in(Singleton.class);

        // Ensure that the event receiver in the gui package is initialized.
        bindListener(new AbstractMatcher<TypeLiteral<?>>() {
            @Override
            public boolean matches(TypeLiteral<?> typeLiteral) {
                return typeLiteral.getRawType() == MainViewController.class;
            }
        }, new TypeListener() {
            @Override
            public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    @Override
                    public void afterInjection(Object i) {
                        MainViewController m = (MainViewController) i;
                        m.initialize(null, null);
                    }
                });
            }
        });

        // Ensure that the event receiver in the net package is initialized. 
        bindListener(new AbstractMatcher<TypeLiteral<?>>() {
            @Override
            public boolean matches(TypeLiteral<?> typeLiteral) {
                return typeLiteral.getRawType() == RabbitsEventReceiver.class;
            }
        }, new TypeListener() {
            @Override
            public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    @Override
                    public void afterInjection(Object i) {
                        RabbitsEventReceiver m = (RabbitsEventReceiver) i;
                        m.init();
                    }
                });
            }
        });
    }

    @Provides
    @Singleton
    WebSocketContainer provideContainer() {
        return ClientManager.createClient();
    }

    /**
     * A hash map which works as a local data map. This can be replaced with a
     * more sophisticated implementation.
     *
     * @return the map interface for your local data
     */
    @Provides
    @Singleton
    @Named("Local Data Map")
    Map<String, Map<String, Object>> provideDataMap() {
        return new HashMap<>();
    }

}
