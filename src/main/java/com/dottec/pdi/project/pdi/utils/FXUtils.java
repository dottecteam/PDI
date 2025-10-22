package com.dottec.pdi.project.pdi.utils;

import javafx.scene.Node;
import javafx.scene.control.*;

public class FXUtils {
    private FXUtils(){};

    public static boolean isFilled(Node node) {
        if (node instanceof TextField textField) {
            return textField.getText() != null && !textField.getText().trim().isEmpty();
        }
        else if (node instanceof TextArea textArea) {
            return textArea.getText() != null && !textArea.getText().trim().isEmpty();
        }
        else if (node instanceof ChoiceBox<?> choiceBox) {
            return choiceBox.getValue() != null;
        }
        else if (node instanceof ComboBox<?> comboBox) {
            return comboBox.getValue() != null;
        }
        else if (node instanceof DatePicker datePicker) {
            return datePicker.getValue() != null;
        }
        else if (node instanceof CheckBox checkBox) {
            return checkBox.isSelected();
        }
        else {
            return node != null;
        }
    }
}
