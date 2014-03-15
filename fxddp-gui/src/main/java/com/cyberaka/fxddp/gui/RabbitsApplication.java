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

import com.cyberaka.fxddp.common.Constants;
import com.cyberaka.fxddp.common.pojo.Rabbit;
import com.cyberaka.fxddp.gui.common.GuiConstants;
import com.cyberaka.fxddp.gui.impl.MainViewController;
import com.google.inject.Guice;
import io.advantageous.ddp.ConnectedMessage;
import io.advantageous.ddp.DDPMessageEndpoint;
import io.advantageous.ddp.ErrorMessage;
import io.advantageous.ddp.subscription.Subscription;
import io.advantageous.ddp.subscription.SubscriptionAdapter;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point into the prototype / proof of concept of JavaFX and DDP
 * integration with the use of Guice and Guava for simplified architecture.
 *
 * @author cyberaka
 */
public class RabbitsApplication extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitsApplication.class);

    @Inject
    private DDPMessageEndpoint endpoint;

    @Inject
    private MainViewController mainController;

    @Inject
    private SubscriptionAdapter adapter;

    {
        Guice.createInjector(new RabbitsApplicationModule()).injectMembers(this);
        endpoint.registerHandler(ErrorMessage.class, message -> LOGGER.error(message.getReason()));
        endpoint.registerHandler(ConnectedMessage.class, message -> {
            try {
                adapter.subscribe(new Subscription(Constants.RABBITS_COLLECTION_NAME, Rabbit.class)
                );
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        new MeteorService().start();
        stage.setTitle(GuiConstants.APP_TITLE);
        Image appIcon = new Image(RabbitsApplication.class.getResource(GuiConstants.APP_ICON_NAME).toExternalForm());
        stage.getIcons().add(appIcon);
        mainController.configureGUI();
        stage.setScene(mainController.getGUI());
        stage.centerOnScreen();
        stage.show();
        mainController.performPostConfigureActions();
    }

    class MeteorService extends Service {

        @Override
        protected Task createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    LOGGER.info("Booting up meteor service!");
                    endpoint.connect(Constants.RABBITS_WEBSOCKET_URL);
                    endpoint.await();
                    LOGGER.warn("disconnected from endpoint");
                    return null;
                }
            };
        }
    }

}
