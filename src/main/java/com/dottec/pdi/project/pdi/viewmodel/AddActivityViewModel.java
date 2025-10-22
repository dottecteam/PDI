package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AddActivityViewModel {
    @FXML private TextField formAddActivityName;
    @FXML private TextField formAddActivityDescription;
    @FXML private ChoiceBox<Tag> formAddActivityTags;
    @FXML private DatePicker formAddActivityDueDate;

    @FXML private VBox formPane;

    @FXML private void initialize(){
        formAddActivityDueDate.setEditable(false);
        formPane.getChildren().stream()
                .filter(node -> node instanceof StackPane)
                .forEach(stack -> {
                    StackPane stackPane = (StackPane) stack;
                    Node input = stackPane.getChildren().get(1);
                    Label label = (Label) stackPane.getChildren().get(0);

                    input.focusedProperty().addListener((obs, oldVal, newVal) -> updateLabel(input, label));
                });
    }

    private void updateLabel(Node node, Label label){
        if(node.isFocused() || FXUtils.isFilled(node)){
            label.getStyleClass().add("formInput-label-focused");
        } else {
            label.getStyleClass().remove("formInput-label-focused");
        }
    }

    @FXML
    void raiseMessage(ActionEvent actionEvent){
        TemplateViewModel.showErrorMessage("Butão Cricad mensagem maior só pra testaro");
    }

}
