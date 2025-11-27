package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.TagController;
import com.dottec.pdi.project.pdi.enums.TagType;
import com.dottec.pdi.project.pdi.model.Tag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class TagsMenuViewModel implements Initializable{
    //Selected Tags Menu
    @FXML private VBox tagMainField;
    @FXML private FlowPane selectedTagsField;
    @FXML private ScrollPane scrollPaneTags;
    @FXML private Button arrowButton;
    @FXML private Label tagsFormInputLabel;
    @FXML private StackPane stackPaneTagsMenu;

    // Choose Tags Menu
    @FXML private ContextMenu tagsMenu;
    @FXML private ScrollPane chooseTagsMenu;
    @FXML private VBox softSkillsVBox;
    @FXML private VBox hardSkillsVBox;

    //Add Tag Menu
    @FXML private VBox addTagMenu;
    @FXML private Button cancelAddTagButton;
    @FXML private Button confirmAddTagButton;
    @FXML private Label skillLabel;
    @FXML private TextField newTagName;

    private List<Tag> selectedTags = new ArrayList<>();
    public List<Tag> getSelectedTags(){
        return selectedTags;
    }
    public void setSelectedTags(List<Tag> tags){this.selectedTags=tags;}

    private List<Tag> editingSelectedTags = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        populateTags();
        addTagMenu.setManaged(false);
        refresh();

        arrowButton.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (arrowButton.isFocused() || !selectedTags.isEmpty()) {
                tagsFormInputLabel.getStyleClass().add("formInput-label-focused");
            } else {
                tagsFormInputLabel.getStyleClass().remove("formInput-label-focused");
            }
            if(arrowButton.getRotate()==0){
                arrowButton.setRotate(180);
            } else {
                arrowButton.setRotate(0);
            }
        });

        arrowButton.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);

    }

    public void refresh(){
        populateTags();
        updateSelectedTagsField();

        Platform.runLater(() -> {
            if(!selectedTags.isEmpty()){
                tagsFormInputLabel.getStyleClass().add("formInput-label-focused");
            }
        });
    }

    @FXML
    private void openTagsMenu(MouseEvent event){
        Bounds bounds = arrowButton.localToScreen(arrowButton.getBoundsInLocal());
        tagsMenu.show(arrowButton, bounds.getMinX(), bounds.getMinY() - 260);
        tagsMenu.getScene().getStylesheets().addAll(tagMainField.getStylesheets());
        selectedTagsField.setPrefWrapLength(selectedTagsField.getWidth());
    }

    private void populateTags(){
        softSkillsVBox.getChildren().clear();
        hardSkillsVBox.getChildren().clear();
        List<Tag> tags = TagController.findAllTags();
        tags.sort(Comparator.comparing(Tag::getName));

        tags.forEach(tag -> {
            CheckBox tagBox = new CheckBox(tag.getName());
            tagBox.getStyleClass().add("tag");
            switch (tag.getType()) {
                case TagType.SOFT -> softSkillsVBox.getChildren().add(tagBox);
                case TagType.HARD -> hardSkillsVBox.getChildren().add(tagBox);
            }

            tagBox.setSelected(selectedTags.contains(tag));

            tagBox.setOnAction(e -> {
                if(selectedTags.contains(tag)){
                    selectedTags.remove(tag);
                    updateSelectedTagsField();
                } else {
                    selectedTags.addFirst(tag);
                    selectedTagsField.getChildren().add(buildSelectedTag(tag));
                }
            });
        });
    }

    public void updateSelectedTagsField(){
        selectedTagsField.getChildren().clear();
        selectedTags.forEach(tag -> {
            Label selectedTag = buildSelectedTag(tag);
            selectedTagsField.getChildren().addFirst(selectedTag);
        });
    }

    private Label buildSelectedTag(Tag tag){
        Region deleteButton = new Region();
        deleteButton.setScaleX(0.7);
        deleteButton.setScaleY(0.7);
        Label selectedTag = new Label(tag.getName(), deleteButton);
        selectedTag.getStyleClass().add("selected-tag");
        deleteButton.getStyleClass().add("x-button");
        deleteButton.setStyle("fx-padding: -3;");
        selectedTag.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        if(tag.getType() == TagType.HARD) {selectedTag.getStyleClass().add("hard-skill");}

        deleteButton.setOnMouseClicked(e -> {
            selectedTagsField.getChildren().remove(selectedTag);
            selectedTags.remove(tag);
            populateTags();
        });

        return selectedTag;
    }

    @FXML
    private void clearSelectedTags(){
        if(!selectedTags.isEmpty()){
            selectedTags.clear();
            updateSelectedTagsField();
            populateTags();
        }
    }

    @FXML
    private void enableAddTag(ActionEvent event){
        chooseTagsMenu.setManaged(false);
        chooseTagsMenu.setVisible(false);
        addTagMenu.setManaged(true);
        addTagMenu.setVisible(true);

        Tag newTag = new Tag();

        confirmAddTagButton.setOnMouseClicked(e -> {
            newTag.setName(newTagName.getText());
            if(skillLabel.getText().equals("Soft Skill")){
                newTag.setType(TagType.SOFT);
            } else {
                newTag.setType(TagType.HARD);
            }

            selectedTags.add(newTag);

            disableAddTag();
            populateTags();
            updateSelectedTagsField();

            TagController.addTag(newTag);
        });

        cancelAddTagButton.setOnMouseClicked(e -> {
            disableAddTag();
        });

    }

    private void disableAddTag(){
        newTagName.clear();
        chooseTagsMenu.setManaged(true);
        chooseTagsMenu.setVisible(true);
        addTagMenu.setManaged(false);
        addTagMenu.setVisible(false);
    }

    @FXML
    private void handleSwitchSkill(MouseEvent event){
        if(skillLabel.getText().equals("Soft Skill")){
            skillLabel.setText("Hard Skill");
        } else {
            skillLabel.setText("Soft Skill");
        }
    }

    public void enableEditing(){
        editingSelectedTags.clear();
        editingSelectedTags.addAll(selectedTags);

        populateTags();
        selectedTagsField.getStyleClass().add("label-editable");
        selectedTagsField.getStyleClass().remove("label-not-editable");
        arrowButton.setManaged(true);
        arrowButton.setVisible(true);
        arrowButton.setMouseTransparent(false);
        updateSelectedTagsField();
    }

    public void disableEditing(){
        tagMainField.getStyleClass().remove("formInput");
        selectedTagsField.getStyleClass().remove("label-editable");
        selectedTagsField.getStyleClass().add("label-not-editable");
        arrowButton.setManaged(false);
        arrowButton.setVisible(false);
        arrowButton.setMouseTransparent(true);
        tagsFormInputLabel.setStyle("-fx-padding: 0;");

        Platform.runLater(() -> {
            selectedTagsField.getChildren().clear();
            selectedTags.forEach(tag -> {
                Label selectedTag = new Label(tag.getName());
                selectedTag.getStyleClass().add("selected-tag");
                selectedTag.setStyle("-fx-padding: 2, 10;");
                if(tag.getType() == TagType.HARD) {selectedTag.getStyleClass().add("hard-skill");}
                selectedTagsField.getChildren().add(selectedTag);
            });
        });
    }

    public void confirmEdit(){
        disableEditing();
    }

    public void cancelEdit(){
        selectedTags.clear();
        selectedTags.addAll(editingSelectedTags);
        updateSelectedTagsField();
        disableEditing();
    }
}
