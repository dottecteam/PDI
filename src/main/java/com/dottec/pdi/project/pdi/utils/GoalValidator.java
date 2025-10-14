package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.model.Goal;
import java.time.LocalDate;

public final class GoalValidator {

    private GoalValidator() {}

    public static boolean isValid(Goal goal) {

        if (goal == null || goal.getName() == null || goal.getDescription() == null || goal.getDeadline() == null) {
            return false;
        }

        boolean isNameValid = StringValidator.descriptionValidate(goal.getName());
        boolean isDescriptionValid = StringValidator.descriptionValidate(goal.getDescription());
        boolean isDateValid = isFutureDate(goal.getDeadline());

        boolean areTagsValid = TagValidator.isListValid(goal.getTags());

        return isNameValid && isDescriptionValid && isDateValid && areTagsValid;
    }

    private static boolean isFutureDate(LocalDate deadline) {
        if (deadline == null) return false;
        return !deadline.isBefore(LocalDate.now());
    }
}