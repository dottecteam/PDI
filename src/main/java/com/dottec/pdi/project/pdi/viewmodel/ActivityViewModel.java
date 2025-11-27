package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.model.Activity;
import com.dottec.pdi.project.pdi.model.Attachment;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.awt.Desktop;
import java.io.IOException;

public class ActivityViewModel {
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private ImageView editButton;
    @FXML
    private ImageView deleteButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label statusLabel;
    @FXML
    private DatePicker deadlineDatePicker;
    @FXML
    private GridPane titledPaneHeader;
    @FXML
    private TitledPane activityTitledPane;

    @FXML
    private Button uploadButton;
    @FXML
    private VBox attachmentsVBox;

    private Activity activity;
    private boolean creatingGoalMode = false;

    public void setCreatingGoalMode(boolean creatingGoalMode) {
        this.creatingGoalMode = creatingGoalMode;
    }

    private GoalViewModel goalViewModel;

    public void setGoalViewModel(GoalViewModel goalViewModel) {
        this.goalViewModel = goalViewModel;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @FXML
    void initialize() {
        deadlineDatePicker.setEditable(false);
        deadlineDatePicker.getEditor().setMouseTransparent(true);
        deadlineDatePicker.setMouseTransparent(true);
        buttonVisible(cancelButton, false);
        buttonVisible(confirmButton, false);
    }

    public void updateFields() {
        nameField.setText(activity.getName());
        descriptionField.setText(activity.getDescription());
        deadlineDatePicker.setValue(activity.getDeadline());

        switch (activity.getStatus()) {
            case completed -> {
                statusLabel.setText("Completo");
                statusLabel.setStyle("-fx-background-color: #6D00A1; -fx-text-fill: white");
            }
            case in_progress -> {
                statusLabel.setText("Em progresso");
                statusLabel.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: #5c5c5c;");
            }
            case canceled -> {
                statusLabel.setText("Cancelado");
                statusLabel.setStyle("-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c");
            }
            case pending -> {
                statusLabel.setText("Pendente");
                statusLabel.setStyle("-fx-background-color: #AF69CD; -fx-text-fill: #5c5c5c;");
            }
            default -> {
                statusLabel.setText("Desconhecido");
                statusLabel.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
            }

        }

        if (!creatingGoalMode) {
            displayAttachments(activity.getAttachments());
        }
    }

    private void displayAttachments(List<Attachment> attachments) {
        attachmentsVBox.getChildren().clear();
        if (attachments == null || attachments.isEmpty()) {
            Label noAttachments = new Label("Nenhum anexo encontrado.");
            noAttachments.setStyle("-fx-font-style: italic; -fx-text-fill: #808080;");
            attachmentsVBox.getChildren().add(noAttachments);
            return;
        }

        for (Attachment attachment : attachments) {
            String fullPath = attachment.getFilePath();
            String fileName = fullPath.substring(fullPath.lastIndexOf('/') + 1);

            // Container HBox para o nome do arquivo e o botão de exclusão
            HBox attachmentBox = new HBox(10);
            attachmentBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(attachmentBox, Priority.ALWAYS);

            // Link para abrir o arquivo
            Hyperlink fileLink = new Hyperlink("🔗 " + fileName);
            fileLink.setStyle("-fx-text-fill: #4B0081;");
            fileLink.setOnAction(e -> handleOpenFile(attachment));

            // Botão/Ícone de Lixeira
            ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/dottec/pdi/project/pdi/static/img/trash.png")));
            deleteIcon.setFitHeight(20);
            deleteIcon.setFitWidth(20);
            deleteIcon.setStyle("-fx-cursor: hand;");

            deleteIcon.setOnMouseClicked(e -> handleDeleteAttachment(attachment));

            attachmentBox.getChildren().addAll(fileLink, deleteIcon);
            attachmentsVBox.getChildren().add(attachmentBox);
        }
    }

    private void handleDeleteAttachment(Attachment attachment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Tem certeza que deseja excluir o anexo '" + attachment.getFilePath().substring(attachment.getFilePath().lastIndexOf('/') + 1) + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = ActivityController.deleteAttachment(attachment);

                if (success) {
                    activity.getAttachments().remove(attachment);

                    displayAttachments(activity.getAttachments());
                    TemplateViewModel.showSuccessMessage("Sucesso!", "Anexo excluído com sucesso.");
                } else {
                    TemplateViewModel.showErrorMessage("Erro ao excluir", "Falha ao excluir o anexo. Tente novamente.");
                }
            }
        });
    }

    private void handleOpenFile(Attachment attachment) {
        String realPath = attachment.getFilePath().substring(1);
        File file = new File(realPath);

        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    // Tenta abrir o arquivo com o programa padrão do sistema
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    TemplateViewModel.showErrorMessage("Erro ao abrir arquivo", "Não foi possível abrir o arquivo. Verifique se há um aplicativo associado a este tipo de arquivo.");
                    ex.printStackTrace();
                }
            } else {
                TemplateViewModel.showErrorMessage("Recurso não suportado", "A abertura de arquivos nativa não é suportada neste sistema operacional.");
            }
        } else {
            TemplateViewModel.showErrorMessage("Arquivo não encontrado", "O arquivo '" + file.getName() + "' não foi encontrado no caminho: " + file.getAbsolutePath());
        }
    }


    private void buttonVisible(Node button, Boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    @FXML
    private void handleEnableEditing() {
        buttonVisible(deleteButton, false);
        buttonVisible(editButton, false);
        buttonVisible(confirmButton, true);
        buttonVisible(cancelButton, true);

        confirmButton.setOnMouseClicked(mouseEvent -> {
            handleConfirmEdit();
        });
        cancelButton.setOnMouseClicked(mouseEvent -> {
            handleCancelEditing();
        });

        nameField.getStyleClass().remove("label-not-editable");
        nameField.getStyleClass().add("label-editable");
        nameField.setMouseTransparent(false);
        nameField.setEditable(true);

        descriptionField.getStyleClass().remove("label-not-editable");
        descriptionField.getStyleClass().add("label-editable");
        descriptionField.setEditable(true);

        deadlineDatePicker.getStyleClass().remove("label-not-editable");
        deadlineDatePicker.getStyleClass().add("label-editable");
        deadlineDatePicker.setMouseTransparent(false);
    }

    @FXML
    private void handleCancelEditing() {
        updateFields();
        disableEditingState();
    }

    @FXML
    private void handleConfirmEdit() {
        if (nameField.getText().trim().isEmpty()) {
            TemplateViewModel.showErrorMessage("O nome não pode estar vazio.");
            return;
        } else if (deadlineDatePicker.getValue().isBefore(LocalDate.now())) {
            TemplateViewModel.showErrorMessage("O prazo deve ser uma data futura.");
            return;
        }

        activity.setName(nameField.getText());
        activity.setDescription(descriptionField.getText());
        activity.setDeadline(deadlineDatePicker.getValue());

        if (!creatingGoalMode) {
            ActivityDAO.update(activity);
        }

        TemplateViewModel.showSuccessMessage("Meta atualizada com sucesso!");

        disableEditingState();
    }

    private void disableEditingState() {
        nameField.setEditable(false);
        nameField.getStyleClass().add("label-not-editable");
        nameField.setMouseTransparent(true);
        nameField.getStyleClass().remove("label-editable");

        descriptionField.setEditable(false);
        descriptionField.getStyleClass().add("label-not-editable");
        descriptionField.getStyleClass().remove("label-editable");

        deadlineDatePicker.getStyleClass().remove("label-editable");
        deadlineDatePicker.getStyleClass().add("label-not-editable");
        deadlineDatePicker.setMouseTransparent(true);

        buttonVisible(editButton, true);
        buttonVisible(deleteButton, true);
        buttonVisible(confirmButton, false);
        buttonVisible(cancelButton, false);
    }

    @FXML
    private void handleEnableDelete() {
        buttonVisible(deleteButton, false);
        buttonVisible(editButton, false);
        buttonVisible(confirmButton, true);
        buttonVisible(cancelButton, true);

        confirmButton.setOnMouseClicked(mouseEvent -> {
            handleConfirmDelete();
        });
        cancelButton.setOnMouseClicked(mouseEvent -> {
            handleCancelEditing();
        });
    }

    @FXML
    private void handleConfirmDelete() {
        if (creatingGoalMode) {
            goalViewModel.removeActivity(activity);
        } else {
            goalViewModel.removeActivity(activity);
            ActivityDAO.delete(activity);
        }
        TemplateViewModel.showSuccessMessage("Atividade excluída com sucesso!");
    }

    @FXML
    private void handleUploadFile() {
        if (creatingGoalMode || activity.getId() == 0) {
            TemplateViewModel.showErrorMessage("Erro de Upload", "É necessário salvar a meta antes de anexar arquivos.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo de comprovação");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos de Imagem", "*.jpg", "*.png", "*.jpeg"),
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Todos os Arquivos", "*.*")
        );


        Stage stage = (Stage) activityTitledPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {

            boolean success = ActivityController.saveAttachment(activity, selectedFile);

            if (success) {

                Attachment newAttachment = new Attachment();
                newAttachment.setFilePath("/uploads/activities/" + activity.getId() + "/" + selectedFile.getName());
                activity.addAttachment(newAttachment);

                displayAttachments(activity.getAttachments());
                TemplateViewModel.showSuccessMessage("Upload de arquivo bem-sucedido!", "O arquivo " + selectedFile.getName() + " foi anexado.");
            } else {
                TemplateViewModel.showErrorMessage("Erro de Upload", "Falha ao salvar o anexo no banco de dados.");
            }
        }
    }
}