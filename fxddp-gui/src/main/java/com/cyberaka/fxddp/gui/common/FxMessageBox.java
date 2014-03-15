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
package com.cyberaka.fxddp.gui.common;

import com.cyberaka.fxddp.gui.RabbitsApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A message box implementation for showing the user an alert with a message.
 *
 * @author cyberaka
 */
public class FxMessageBox extends Stage {

    public FxMessageBox(Stage owner, String message, String title) {
        VBox vb = new VBox();
        vb.setPadding(new Insets(5));
        vb.setSpacing(5);
        Button okButton = new Button(GuiConstants.LABEL_OK);
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FxMessageBox.this.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().add(new FxWrappedText(message));
        vb.getChildren().addAll(msg, bp);

        Scene scene = new Scene(vb);
        setTitle(title);
        initStyle(StageStyle.DECORATED);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setResizable(false);

        // A test for CSS based UI rendering. 
        //scene.getStylesheets().add(RabbitsApp.class.getResource(GUIConstants.CSS_FILE_NAME).toExternalForm());
        Image appIcon = new Image(RabbitsApplication.class.getResource(GuiConstants.APP_ICON_NAME).toExternalForm());
        setScene(scene);
        this.getIcons().add(appIcon);
    }

    public void showDialog() {
        sizeToScene();
        centerOnScreen();
        showAndWait();
    }
}
