package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.ActivityController;
import com.dottec.pdi.project.pdi.controllers.GoalController;
import com.dottec.pdi.project.pdi.dao.ActivityDAO;
import com.dottec.pdi.project.pdi.model.Activity;

import com.dottec.pdi.project.pdi.model.Attachment;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import javafx.fxml.FXML;
import javafx.geometry.Pos;

import com.dottec.pdi.project.pdi.model.Tag;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.layout.*;

import java.time.LocalDate;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.awt.Desktop;
import java.util.Optional;

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

    // REMOVIDO: @FXML private Label statusLabel;

    // NOVO: MenuButton para Status
    @FXML
    private MenuButton statusMenu;

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

    @FXML
    private VBox activityBody;


    private Activity activity;
    private boolean creatingGoalMode = false;

    public void setCreatingGoalMode(boolean creatingGoalMode) {
        this.creatingGoalMode = creatingGoalMode;
    }

    private GoalViewModel goalViewModel;

    public void setGoalViewModel(GoalViewModel goalViewModel) {
        this.goalViewModel = goalViewModel;
    }

    private TagsMenuViewModel tagsMenuViewModel;

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
        loadTagsMenu();
        titledPaneHeader.setMinWidth(activityTitledPane.getWidth());

        // Configuração inicial do MenuButton de Status
        if (statusMenu != null) {
            statusMenu.getStyleClass().add("label-status");
            statusMenu.setMouseTransparent(true); // Inicialmente não clicável
            statusMenu.setFocusTraversable(false);
        }
    }

    private void loadTagsMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/dottec/pdi/project/pdi/views/TagsMenu.fxml"));
            Parent root = loader.load();
            tagsMenuViewModel = loader.getController();
            activityBody.getChildren().add(1, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFields() {
        nameField.setText(activity.getName());
        descriptionField.setText(activity.getDescription());
        deadlineDatePicker.setValue(activity.getDeadline());

        tagsMenuViewModel.disableEditing();
        tagsMenuViewModel.setSelectedTags(activity.getTags());
        tagsMenuViewModel.refresh();

        // Lógica de Status
        if (statusMenu != null) {
            updateStatusDisplay(activity.getStatus().name());

            statusMenu.getItems().clear();
            statusMenu.setText(translateActivityStatus(activity.getStatus().name()));

            for (ActivityStatus statusOption : ActivityStatus.values()) {
                if (statusOption != activity.getStatus()) {
                    MenuItem item = new MenuItem(translateActivityStatus(statusOption.name()));
                    item.setOnAction(e -> handleActivityStatusChange(statusOption));
                    statusMenu.getItems().add(item);
                }
            }
        }

        if (!creatingGoalMode) {
            displayAttachments(activity.getAttachments());
        }
    }

    // NOVO MÉTODO: Lógica de mudança de status da atividade
    private void handleActivityStatusChange(ActivityStatus newStatus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmar mudança de status para " + translateActivityStatus(newStatus.name()) + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            activity.setStatus(newStatus);

            if (!creatingGoalMode) {
                ActivityController.updateActivity(activity);
                TemplateViewModel.showSuccessMessage("Status da atividade atualizado!");
            } else {
                TemplateViewModel.showSuccessMessage("Status da atividade alterado (será salvo com a meta).");
            }

            // Força o GoalViewModel a recarregar as atividades
            if (goalViewModel != null) {
                goalViewModel.populateActivities();
            }

            updateStatusDisplay(newStatus.name());
        }
    }

    // NOVO MÉTODO: Tradução de Status de Atividade
    private String translateActivityStatus(String status) {
        return switch (status.toLowerCase()) {
            case "completed" -> "Completo";
            case "in_progress" -> "Em progresso";
            case "pending" -> "Pendente";
            case "canceled" -> "Cancelado";
            default -> status;
        };
    }

    // NOVO MÉTODO: Atualiza cor e texto do status do MenuButton
    private void updateStatusDisplay(String status) {
        String colorStyle = "";
        String text = translateActivityStatus(status);

        switch (status.toLowerCase()) {
            case "completed" -> colorStyle = "-fx-background-color: #6D00A1; -fx-text-fill: white";
            case "in_progress" -> colorStyle = "-fx-background-color: #AF69CD; -fx-text-fill: white;";
            case "canceled" -> colorStyle = "-fx-background-color: #E6CCEF; -fx-text-fill: #5c5c5c";
            case "pending" -> colorStyle = "-fx-background-color: #AF69CD; -fx-text-fill: #5c5c5c;";
            default -> colorStyle = "-fx-background-color: grey; -fx-text-fill: white;";
        }

        if (statusMenu != null) {
            statusMenu.setText(text);
            statusMenu.setStyle(colorStyle);
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

        tagsMenuViewModel.enableEditing();

        deadlineDatePicker.getStyleClass().remove("label-not-editable");
        deadlineDatePicker.getStyleClass().add("label-editable");
        deadlineDatePicker.setMouseTransparent(false);

        // Habilita o MenuButton de Status
        if (statusMenu != null) {
            statusMenu.setMouseTransparent(false);
        }
    }

    @FXML
    private void handleCancelEditing() {
        updateFields();
        disableEditingState();
        tagsMenuViewModel.cancelEdit();
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

        tagsMenuViewModel.confirmEdit();

        activity.setName(nameField.getText());
        activity.setDescription(descriptionField.getText());
        activity.setDeadline(deadlineDatePicker.getValue());

        if (!creatingGoalMode) {
            ActivityController.updateActivity(activity);
            Goal goal = activity.getGoal();
            if(goal.getDeadline() == null) {
                goal.setDeadline(activity.getDeadline());
                GoalController.updateGoal(goal);
            } else if(activity.getDeadline().isAfter(goal.getDeadline())){
                goal.setDeadline(activity.getDeadline());
                GoalController.updateGoal(goal);
            }
        }

        // Força a atualização do GoalViewModel para refletir a mudança no prazo/nome da Atividade
        if (goalViewModel != null) {
            goalViewModel.populateActivities();
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

        // Desabilita o MenuButton de Status
        if (statusMenu != null) {
            statusMenu.setMouseTransparent(true);
        }

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