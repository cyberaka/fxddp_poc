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
package com.cyberaka.fxddp.gui.impl;

import com.cyberaka.fxddp.common.event.NewRabbitAddedEvent;
import com.cyberaka.fxddp.common.event.RabbitsAddedEvent;
import com.cyberaka.fxddp.common.event.RabbitsRemovedEvent;
import com.cyberaka.fxddp.common.event.RabbitsUpdatedEvent;
import com.cyberaka.fxddp.common.pojo.Rabbit;
import com.cyberaka.fxddp.common.pojo.RabbitInfo;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class assembles a UI which shows the data entry form as well as the data
 * view.
 *
 * @author cyberaka
 */
@Singleton
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    @Inject
    private EventBus eventBus;

    private Map<String, RabbitInfo> itemMap = new HashMap<>();

    private ObservableList<RabbitInfo> rabbitList = FXCollections.observableArrayList();

    private Scene scene;
    private FormView formView;
    private DataView dataView;

    @Subscribe
    public void handleAdded(final RabbitsAddedEvent event) {
        Platform.runLater(() -> {
            LOGGER.info("handleAdded() >> " + event.toString());
            Rabbit newRabbit = event.getRabbit();
            RabbitInfo newRabbitInfo = new RabbitInfo(newRabbit.getName(), Integer.parseInt(newRabbit.getAge()));
            rabbitList.add(newRabbitInfo);
            itemMap.put(event.getKey(), newRabbitInfo);
        });
    }

    @Subscribe
    public void handleRemoved(final RabbitsRemovedEvent event) {
        Platform.runLater(() -> {
            LOGGER.info("handleRemoved() >> " + event.toString());
            RabbitInfo removedInfo = itemMap.remove(event.getKey());
            rabbitList.remove(removedInfo);
        });
    }

    @Subscribe
    public void handleModified(final RabbitsUpdatedEvent event) {
        Platform.runLater(() -> {
            LOGGER.info("handleModified() >> " + event.toString());
            RabbitInfo foundInfo = itemMap.get(event.getKey());
            int index = rabbitList.lastIndexOf(foundInfo);
            Rabbit modifiedRabbit = event.getRabbit();
            RabbitInfo modifiedRabbitInfo = new RabbitInfo(modifiedRabbit.getName(), Integer.parseInt(modifiedRabbit.getAge()));
            rabbitList.set(index, modifiedRabbitInfo);
            itemMap.put(event.getKey(), modifiedRabbitInfo);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        eventBus.register(this);
    }

    public Scene getGUI() {
        return scene;
    }

    public void configureGUI() {
        Group root = new Group();
        scene = new Scene(root, 500, 500);
        // A test for CSS based UI rendering. 
        // scene.getStylesheets().add(RabbitsApp.class.getResource(GUIConstants.CSS_FILE_NAME).toExternalForm());
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.prefWidthProperty().bind(scene.widthProperty());
        splitPane.prefHeightProperty().bind(scene.heightProperty());
        formView = new FormView();
        formView.configureGUI(this);
        splitPane.getItems().add(formView.getGUI());
        dataView = new DataView();
        dataView.configureGUI(this);
        splitPane.getItems().add(dataView.getGUI());
        splitPane.setDividerPositions(0.20f, 0.80f);
        HBox hbox = new HBox();
        hbox.getChildren().add(splitPane);
        root.getChildren().add(hbox);
    }

    public void performPostConfigureActions() {
        formView.performPostConfigureActions();
        dataView.performPostConfigureActions();
    }

    public void listUpdated() {
        dataView.performPostConfigureActions();

    }

    public ObservableList<RabbitInfo> getDataList() {
        return rabbitList;
    }

    public void addRabbit(RabbitInfo newData) {
        Rabbit rabbit = new Rabbit();
        rabbit.setAge("" + newData.getAge());
        rabbit.setName(newData.getName());
        LOGGER.info("addRabbit() >> Adding new rabbit >> " + rabbit);
        eventBus.post(new NewRabbitAddedEvent("" + System.currentTimeMillis(), rabbit));
    }
}
