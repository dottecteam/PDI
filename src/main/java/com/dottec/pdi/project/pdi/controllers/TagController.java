package com.dottec.pdi.project.pdi.controllers;

import com.dottec.pdi.project.pdi.dao.TagDAO;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.enums.TagType;

import java.util.List;

public final class TagController {

    private TagController() {}

    public static void addTag(Tag tag) {
        TagDAO.insert(tag);
    }

    public static void deleteTag(int id) {
        TagDAO.deleteById(id);
    }

    public static Tag findTagById(int id) {
        return TagDAO.findById(id);
    }

    public static List<Tag> findAllTags() {
        return TagDAO.readAll();
    }

    public static void updateTag(int id, String newName, TagType newType) {
        Tag existingTag = TagDAO.findById(id);
        if (existingTag != null) {
            existingTag.setName(newName);
            existingTag.setType(newType);
            TagDAO.update(existingTag);
        } else {
            System.err.println("Erro: Tag com ID " + id + " não encontrada para atualização.");
        }
    }
}