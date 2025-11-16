package com.dottec.pdi.project.pdi.viewmodel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterMenuViewModel extends VBox {
    Popup popup = new Popup();
    ScrollPane scrollPane = new ScrollPane();
    Button confirmFilterButton = new Button("Filtrar");
    public Button getConfirmFilterButton(){
        return confirmFilterButton;
    }

    public FilterMenuViewModel(){
        setSpacing(10);
        setStyle("-fx-background-color: f6f6f6;");

        Label headerLabel = new Label("Filtros");
        headerLabel.getStyleClass().add("mid-label");
        headerLabel.setStyle("-fx-text-fill: #4B0081");
        headerLabel.setAlignment(Pos.CENTER_LEFT);
        confirmFilterButton.getStyleClass().add("confirm-filter-button");
        confirmFilterButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> popup.hide());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(headerLabel, spacer, confirmFilterButton);
        header.setAlignment(Pos.CENTER_RIGHT);
        getChildren().add(header);

        scrollPane.getStylesheets().add(getClass().getResource("/com/dottec/pdi/project/pdi/static/style/style_template.css").toExternalForm());
        scrollPane.getStyleClass().add("filter-menu");
        scrollPane.setMaxHeight(400);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(this);
        scrollPane.setFitToWidth(true);
        popup.getContent().add(scrollPane);
        popup.setHideOnEscape(true);
        popup.setAutoHide(true);
    }

    public void addFilterField(String fieldName, Node... items){
        addFilterField(fieldName, Arrays.asList(items));
    }

    public void addFilterField(String fieldName, List<Node> items){
        Label label = new Label(fieldName);
        Separator separator = new Separator();
        separator.setPrefWidth(300);
        VBox header = new VBox();
        header.getChildren().addAll(label, separator);

        VBox content = new VBox();
        content.setSpacing(5);
        content.getChildren().addAll(items);

        TitledPane titledPane = new TitledPane();
        titledPane.setGraphic(header);
        titledPane.setContent(content);
        this.getChildren().add(titledPane);
    }

    public void show(Node node){
        popup.show(node.getScene().getWindow(),
                node.localToScreen(node.getBoundsInLocal()).getMinX() - 200,
                node.localToScreen(node.getBoundsInLocal()).getMaxY()+10
        );
    }
}
