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

import com.cyberaka.fxddp.common.Utils;
import com.cyberaka.fxddp.common.pojo.RabbitInfo;
import com.cyberaka.fxddp.gui.common.FxMessageBox;
import com.cyberaka.fxddp.gui.common.GuiConstants;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * This class configures the data entry form and renders it on the view.
 *
 * @author cyberaka
 */
public class FormView implements EventHandler<KeyEvent> {

    private TextField nameFld;
    private TextField ageFld;
    private Button saveButton;
    private GridPane gridPaneView;
    private MainViewController controller;

    public GridPane getGUI() {
        return gridPaneView;
    }

    public void configureGUI(MainViewController controller) {
        this.controller = controller;
        gridPaneView = new GridPane();
        gridPaneView.setPadding(new Insets(5));
        gridPaneView.setHgap(5);
        gridPaneView.setVgap(5);
        Label nameLbl = new Label(GuiConstants.LABEL_NAME);
        nameFld = new TextField();
        Label ageLbl = new Label(GuiConstants.LABEL_AGE);
        ageFld = new TextField();
        saveButton = new Button(GuiConstants.LABEL_SAVE);
        saveButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent t) {
                performSave();
            }
        });
        nameFld.setTooltip(new Tooltip(GuiConstants.TOOLTIP_NAME));
        ageFld.setTooltip(new Tooltip(GuiConstants.TOOLTIP_AGE));
        saveButton.setTooltip(new Tooltip(GuiConstants.TOOLTIP_SAVE));
        nameFld.setOnKeyPressed(this);
        ageFld.setOnKeyPressed(this);
        saveButton.setOnKeyPressed(this);
        saveButton.setMnemonicParsing(true);
        GridPane.setHalignment(nameLbl, HPos.RIGHT);
        gridPaneView.add(nameLbl, 0, 0);
        GridPane.setHalignment(ageLbl, HPos.RIGHT);
        gridPaneView.add(ageLbl, 0, 1);
        GridPane.setHalignment(nameFld, HPos.LEFT);
        GridPane.setHgrow(nameFld, Priority.ALWAYS);
        gridPaneView.add(nameFld, 1, 0);
        GridPane.setHalignment(ageFld, HPos.LEFT);
        GridPane.setHgrow(ageFld, Priority.ALWAYS);
        gridPaneView.add(ageFld, 1, 1);
        GridPane.setHalignment(saveButton, HPos.RIGHT);
        gridPaneView.add(saveButton, 1, 2);

    }

    public void performPostConfigureActions() {
        setSaveAccelerator();
        nameFld.requestFocus();
    }

    private void setSaveAccelerator() {
        Scene scene = saveButton.getScene();
        if (scene != null) {
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                    new Runnable() {
                        @Override
                        public void run() {
                            performSave();
                        }
                    }
            );
        }

    }

    private void showMessageBox(String message) {
        if (message != null) {
            Scene scene = saveButton.getScene();
            if (scene != null) {
                Stage stage = (Stage) scene.getWindow();
                FxMessageBox msgBox = new FxMessageBox(stage, message, GuiConstants.ALERT_TITLE);
                msgBox.show();
            }
        }

    }

    private void performSave() {
        String nameStr = nameFld.getText();
        String ageStr = ageFld.getText();
        if (!Utils.isValidString(nameStr)) {
            showMessageBox(GuiConstants.INVALID_NAME);
            nameFld.requestFocus();
            return;
        }
        if (!Utils.isValidInt(ageStr)) {
            showMessageBox(GuiConstants.INVALID_AGE);
            ageFld.requestFocus();
            return;
        }

        int age = Utils.getInt(ageStr);
        nameStr = nameStr.trim();
        RabbitInfo newData = new RabbitInfo(nameStr, age);
        controller.addRabbit(newData);

        performPostSaveActions();
        /*
         new Thread(controller.getSaveWorker(newData)).start();
         */
    }

    @Override
    public void handle(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER && keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

            if (keyEvent.getSource() == nameFld) {
                ageFld.requestFocus();
            }
            if (keyEvent.getSource() == ageFld) {
                saveButton.requestFocus();
            }
            if (keyEvent.getSource() == saveButton) {
                performSave();

            }
            keyEvent.consume();
        }
    }

    public void performPostSaveActions() {
        nameFld.clear();
        ageFld.clear();
        nameFld.requestFocus();
    }

}
