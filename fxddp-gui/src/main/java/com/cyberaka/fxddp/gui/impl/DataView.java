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

import com.cyberaka.fxddp.common.pojo.RabbitInfo;
import com.cyberaka.fxddp.gui.common.GuiConstants;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * This class configures the table and renders it on the view.
 *
 * @author cyberaka
 */
public class DataView {

    private TableView<RabbitInfo> tableView;
    private GridPane gridPaneView;

    public GridPane getGUI() {
        return gridPaneView;
    }

    public void configureGUI(MainViewController controller) {
        gridPaneView = new GridPane();
        gridPaneView.setPadding(new Insets(5));
        gridPaneView.setHgap(5);
        gridPaneView.setVgap(5);
        tableView = new TableView<>();

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(controller.getDataList());
        TableColumn<RabbitInfo, String> nameCol = new TableColumn<>(GuiConstants.LABEL_NAME);
        nameCol.setEditable(true);
        nameCol.setCellValueFactory(new PropertyValueFactory(RabbitInfo.PROPERTY_NAME));
        nameCol.setPrefWidth(tableView.getPrefWidth() / 2);
        TableColumn<RabbitInfo, String> ageCol = new TableColumn<>(GuiConstants.LABEL_AGE);
        ageCol.setCellValueFactory(new PropertyValueFactory(RabbitInfo.PROPERTY_AGE));
        ageCol.setPrefWidth(tableView.getPrefWidth() / 2);
        ageCol.setEditable(true);
        tableView.getColumns().setAll(nameCol, ageCol);
        GridPane.setHgrow(tableView, Priority.ALWAYS);

        gridPaneView.add(tableView, 0, 0);
        tableView.setItems(controller.getDataList());
    }

    public void performPostConfigureActions() {
    }

}
