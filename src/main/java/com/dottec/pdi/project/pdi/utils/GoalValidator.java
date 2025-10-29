package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.model.Goal;
import java.time.LocalDate;

public final class GoalValidator {

    private GoalValidator() {}

    public static boolean isValid(Goal goal) {

        if (goal == null || goal.getName() == null || goal.getDeadline() == null) {
            return false;
        }

        boolean isDateValid = isFutureDate(goal.getDeadline());
        boolean areTagsValid = true;
        if(!goal.getTags().isEmpty()) {
            areTagsValid = TagValidator.isListValid(goal.getTags());
        }

        return isDateValid && areTagsValid;
    }

    private static boolean isFutureDate(LocalDate deadline) {
        if (deadline == null) return false;
        return !deadline.isBefore(LocalDate.now());
    }
}