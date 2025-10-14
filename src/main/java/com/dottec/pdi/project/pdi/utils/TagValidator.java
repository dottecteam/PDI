package com.dottec.pdi.project.pdi.utils;

import com.dottec.pdi.project.pdi.model.Tag;
import java.util.List;

public final class TagValidator {

    private TagValidator() {}

    public static boolean isValid(Tag tag) {
        if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean isListValid(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return true;
        }
        return tags.stream().allMatch(TagValidator::isValid);
    }
}