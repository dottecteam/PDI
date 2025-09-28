package com.dottec.pdi.project.pdi.viewsController;

import com.dottec.pdi.project.pdi.controllers.CollaboratorController;
import com.dottec.pdi.project.pdi.enums.Status;
import com.dottec.pdi.project.pdi.model.Collaborator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class CollaboratorGoalsController {
    private Collaborator collaborator;
    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    private CollaboratorController collaboratorController = new CollaboratorController();


    //Collaborator Informations
    @FXML
    private TextField nameField;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField roleField;
    @FXML
    private TextField departmentField;
    @FXML
    private TextField experienceField;
    @FXML
    private TextField observationsField;

    //Buttons
    @FXML
    private ImageView editButton;
    @FXML
    private Button confirmEditButton;

    @FXML
    private void initialize(){
        updateFields();
    }

    private void updateFields(){
        if(collaborator == null) return;
        if (collaborator != null) {
        nameField.setText(collaborator.getName());
        emailField.setText(collaborator.getEmail());
        cpfField.setText(collaborator.getCpf());
        departmentField.setText("Desenvolvimento");
        roleField.setText("Desenvolvedor");
        experienceField.setText(collaborator.getExperience());
        observationsField.setText(collaborator.getObservations());
        } else {
            editButton.setDisable(true);
        }
    }


    public void enableFieldsEditing(MouseEvent event) {
        confirmEditButton.setDisable(true);

        for(TextField textField : Arrays.asList(nameField, roleField, departmentField, experienceField, observationsField)){
            textField.setEditable(true);
            textField.getStyleClass().remove("label-not-editable");
            textField.getStyleClass().add("label-editable");

            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    confirmEditButton.setDisable(false);;
                } else {
                    confirmEditButton.setDisable(true);
                }
            });
        }


        confirmEditButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event2 -> {
            updateCollaborator();
        });

        editButton.setVisible(false);
        editButton.setManaged(false);
        confirmEditButton.setManaged(true);
        confirmEditButton.setVisible(true);

    }

    public void disableFieldsEditing(MouseEvent event){
        for(TextField textField : Arrays.asList(nameField, roleField, departmentField, experienceField, observationsField)){
            textField.setEditable(false);
            textField.getStyleClass().add("label-not-editable");
            textField.getStyleClass().remove("label-editable");
        }
        editButton.setVisible(true);
        editButton.setManaged(true);
        confirmEditButton.setManaged(false);
        confirmEditButton.setVisible(false);
    }

    private void updateCollaborator(){
        collaborator.setName(nameField.getText());
        collaborator.setCpf(cpfField.getText());
        collaborator.setEmail(emailField.getText());
        //Atualizar para o id do role quando a lógica for criada
        collaborator.setRole(1);
        //Atualizar para o id do department quando a lógica for criada
        collaborator.setDepartment(1);
        collaborator.setExperience(experienceField.getText());
        collaborator.setObservations(observationsField.getText());

        collaboratorController.updateCollaborator(this.collaborator);
    }

}